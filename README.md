<div align="center">
  <h1>Searck</h1>

[![fabric-api](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api)
[![fabric-language-kotlin](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/requires/fabric-language-kotlin_vector.svg)](https://modrinth.com/mod/fabric-language-kotlin)

[![discord-plural](https://raw.githubusercontent.com/intergrav/devins-badges/refs/heads/v3/assets/cozy/social/discord-plural_vector.svg)](https://tiazzz.me/discord)

**Search right here and right now**
</div>

## About

Searck is a simple Minecraft mod that adds a Satisfactory-style,
promptly available search bar. Just press N and a sleek, true-to-Minecraft
search bar will swiftly be pulled up. 

Features include:

- Making big calculations
  - We have support ranging from simple addition, all the way to tangents and even `atan2` functions when used correctly! All following standard PEMDAS rules and blazingly fast using the Shunting Yard Algorithm!
- Looking up any block or item in the registry and displaying its icon and name
- Doing "quick actions" by pressing Enter:
  - Quickly swapping between items in your currently selected slot if you have the selected item in your inventory (indicated by a yellow outline in the search results list!)
  - Opening the info screen: viewing an item's icon, name and tooltip without needing to have it in your inventory
  - Pushing the outcome of the calculation to the search bar
- Opening the recipes or usages of the item
- Sleek, in-game config screen using MidnightLib
  - With lots of option to configure as well at that! Choose a different item indexer, or even appoint to a custom one! Want to instantly open a recipe viewer from the quick action? Change it here!
- Coded fully in Kotlin
- Available for a great range of Minecraft versions, and actively maintained to support the latest versions

The search menu aims to be as true to Minecraft as possible, making it great for
any modpack, especially Vanilla+ ones! So, get right to searchin'!

## Installing
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/searck/versions)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/Tywrap-Studios/Searck/releases)

## For developers

Searck provides small bits for developers to add on to the mod if they want to,
designed to be as simple as possible for people to understand.

### Gradle Setup ![Release](https://maven.tiazzz.me/api/badge/latest/releases/org/tywrapstudios/searck?color=40c14a&name=Maven&prefix=v)

#### Groovy

```groovy
repositories {
    maven {
        name = "Tywrap Studios Releases"
        url = "https://maven.tiazzz.me/releases"
    }
}

dependencies {
    implementation "org.tywrapstudios:searck:${project.searck_version}"
}
```

#### Kotlin DSL

```kotlin
repositories {
    maven("https://maven.tiazzz.me/releases") {
        name = "Tywrap Studios Releases"
    }
}

dependencies {
    implementation("org.tywrapstudios:searck:${project.searck_version}")
}
```

Make a value in your `gradle.properties` called `searck_version` and set it to the version you want to use:
```properties
searck_version=<VERSION>
```

Alternatively, you can use a version catalogue with `libs.versions.toml`:

```toml
[versions]
searck = "<VERSION>"

[libraries]
searck = { module = "org.tywrapstudios:searck", version.ref = "searck" }
```

```kotlin
/* repositories */
dependencies {
    implementation(libs.searck)
}
```

The Main Maven repository is self-hosted and may occasionally have an outage. Check our
official [Status Page](https://status.tiazzz.me) to check. In the case it is down, there
is a backup repository available
at `https://repo.repsy.io/itstiazzz/maven`. Simply replace the
URL in `repositories` with this one, or add it as a second repository.

### Implementation

#### Adding calculation operators

Operators implement the `IOperator` interface, a recommended implementation
of this is through the use of an `enum` like in Searck itself.
  
Simply provide all the values:  
`val associativity: Associativity`: See [this Wikipedia page](https://en.wikipedia.org/wiki/Operator_associativity)  
`val precedence: Int`: The higher the number, the higher the precedence when it comes to calculation order (a.k.a. PEMDAS). Check Searck's `Operators` enum for predefined precedences.  
`val symbol: String`: The "symbol" of the operator, like `+` or `-`. This is what the user will type in to use the operator. For functions, do NOT include `()` in the `symbol`!  
`val singleOperand: Boolean`: Whether the operator takes in just one operand, like `sqrt()` or `tan()`.  
`val operation: Operation`: `Operation` is actually a function, it takes in two doubles, and outputs a single double. In the lambda function you can define what the actual operation is when done on the two operands. If the operator is `singleOperand`, both values will be the same, however, it is recommended to use the `right` value, as most `singleOperand` operators are considered right-associative.  
  
To allow users to actually use the operators, you need to "register" them in a sense. 
"Registering" them is actually just adding the values to a few maps in `ShuntingYard`.  
`ShuntingYard.OPS`, map ALL your new operators here, functions, actual operators, whatever. It all goes in here! Map the `symbol` to the `IOperator` object.  
`ShuntingYard.FUNCTIONS`, map all operators in here that are functions (multi-character operators). Map the `symbol` to the `IOperator` object.  
`ShuntingYard.FUNCTION_CHARS`, add every character of your functions in here. This is important! What we mean by this, is that you break apart every 
single one of your functions, and grab every character of it, and add it to this set. E.g. for `sqrt`, we added `s`, `q`, `r` and `t`.  
  
#### Adding a custom `ItemIndexer`

In order for the search menu to be able to look up every item or block, we index it and
provide a couple of functions for it to properly search the items.

Indexers implement the `ItemIndexer` interface, which already has some KDocs to get
you started.

`ItemIndexer`:  
Interface that can be used to make different "indexers" for use during search.

Implementation has to be a class with an empty constructor. It
cannot be an `object` or have parameters, because this will break the reflection
used in `ItemIndex.getActive`.

You have to implement a few functions:

`index(): Unit`:  
Run logic to "index" items to names (or vice versa, just make sure you can
properly implement `getNames` and `getItemsForName`).

This gets called when Minecraft resources are reloaded (for the language)
or if the indexer is fetched using `ItemIndex.getActive` and has not yet
been indexed at least once. It can be run at any arbitrary time during the
lifecycle though, so beware of that in your implementation.

When this indexer is the selected indexer in config, this method is guaranteed to be called
twice during initial resources load. Once because it's technically not yet been indexed, and then once
because the resource manager has also technically been reloaded.

`getNames(): List<String>`:  
Returns a list of all the names (as Strings) of the indexed `ItemLike`s.

`getItemsForName(): List<ItemLike>`:  
Returns a list of `ItemLike`s which name match the name given.  
Parameter: `name` - Name of the `ItemLike`(s) searched for

#### Using the indexer
In config, select the Custom Indexer (`CUSTOM`) as your search
indexer. Then, provide in the text box below, the package and class name to your custom
indexer. For example: `com.example.search.ExampleIndexer`
