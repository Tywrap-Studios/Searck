plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
    kotlin("jvm") version "2.4.0"
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")

    maven("https://maven.midnightdust.eu/releases")
    maven("https://maven.terraformersmc.com/")

    maven("https://maven.blamejared.com/")
    maven("https://maven.shedaniel.me")
}

dependencies {
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"]))
    }

    fun includeImplementation(notation: Any) {
        implementation(notation)
        include(notation)
    }

    fun includeModImplementation(notation: Any) {
        modImplementation(notation)
        include(notation)
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    // Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}")

    // FuzzyKot
    includeImplementation("com.github.terrakok:fuzzykot:${property("deps.fuzzykot")}")
    // MidnightLib
    if (sc.current.parsed >= "1.21.11") {
        includeModImplementation("eu.midnightdust:midnightlib:${property("deps.midnightlib")}")
    } else {
        modImplementation("eu.midnightdust:midnightlib:${property("deps.midnightlib")}")
    }

    // Compile against the JEI API but do not include it at runtime
    modCompileOnly("mezz.jei:jei-${sc.current.version}-fabric-api:${property("deps.jei")}")
    // At runtime, use the full JEI jar for Fabric
    modRuntimeOnly("mezz.jei:jei-${sc.current.version}-fabric:${property("deps.jei")}")

    // EMI (only exists on 1.21(.1) right now
    if (sc.current.parsed <= "1.21.1") {
        modCompileOnly("dev.emi:emi-fabric:${property("deps.emi")}:api")
        modLocalRuntime("dev.emi:emi-fabric:${property("deps.emi")}")
    }

    // Compile against the REI API but do not include it at runtime
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${property("deps.rei")}")
    // At runtime, use the full REI jar for Fabric
    modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${property("deps.rei")}")

    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
//    if (sc.current.parsed < "26.1") {
//        fapi("fabric-key-binding-api-v1")
//    } else {
//        fapi("fabric-key-mapping-api-v1",
//            "fabric-resource-loader-v1",
//            "fabric-permission-api-v1",
//        )
//    }
//    fapi(
//        "fabric-lifecycle-events-v1",
//        "fabric-data-attachment-api-v1",
//        "fabric-item-api-v1",
//        "fabric-block-api-v1",
//    )
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
    accessWidenerPath = sc.process(
        rootProject.file("src/main/resources/${property("mod.id")}.ct"),
        "build/processed.ct"
    )

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run") // Shares the run directory between versions
        jvmArguments.add("-Dmixin.debug.export=true") // Exports transformed classes for debugging
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

kotlin {
    jvmToolchain(requiredJava.majorVersion.toInt())
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(requiredJava.majorVersion))
    }
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}