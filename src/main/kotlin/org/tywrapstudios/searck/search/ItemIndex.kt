package org.tywrapstudios.searck.search

import org.tywrapstudios.searck.Searck
import org.tywrapstudios.searck.config.IndexerOption
import org.tywrapstudios.searck.config.SearckConfig
import net.minecraft.server.packs.PackType

//? <26.1
//import org.tywrapstudios.searck.platform.registerReloadListener

//? >= 1.21.11 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener

//?} else {
/*import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Unit
import net.minecraft.util.profiling.ProfilerFiller
import org.tywrapstudios.searck.platform.ResourceLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

*///?}

//? >=1.21.11 {
object ItemIndex : ResourceManagerReloadListener {
//?} else {
/*object ItemIndex : IdentifiableResourceReloadListener {
*///?}
    private val hasIndexed = mutableMapOf<ItemIndexer, Boolean>()

    //? >=1.21.11 {
    override fun onResourceManagerReload(resourceManager: ResourceManager) {
    //?} else {
    /*override fun reload(
        preparationBarrier: PreparableReloadListener.PreparationBarrier,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller,
        profilerFiller2: ProfilerFiller,
        executor: Executor,
        executor2: Executor
    ): CompletableFuture<Void> {
    *///?}
        //? <1.21.11 {
        /*return preparationBarrier.wait<Unit>(Unit.INSTANCE).thenRunAsync( {
            profilerFiller2.startTick()
            profilerFiller2.push("listener")
            *///?}
            Searck.LOGGER.info("Reloaded: Indexing items...")
            getActive().index()
            //? <1.21.11 {
            /*profilerFiller2.pop()
            profilerFiller2.endTick()
        }, executor2)
        *///?}
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getActive(): ItemIndexer {
        val chosen = SearckConfig.indexer
        val active = when (chosen) {
            IndexerOption.REGISTRY -> RegistryIndexer
            IndexerOption.LANG_FILE -> LangIndexer
            else -> {
                val clazz = try {
                    Class.forName(SearckConfig.customIndexer).getConstructor().newInstance()
                } catch (e: Throwable) {
                    throw RuntimeException("Custom indexer ${SearckConfig.customIndexer} could not be found, exception during reflection", e)
                }
                clazz as? ItemIndexer
                    ?: throw RuntimeException("Returned class ${SearckConfig.customIndexer} is not of type org.tywrapstudios.searck.search.ItemIndexer or could not be loaded: $clazz")
            }
        }

        if (!hasIndexed.getOrPutIfMissing(active) { false }) {
            active.index()
            hasIndexed[active] = true
        }
        return active
    }

    fun init() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(Searck.id("item_index"), this)
    }

    //? <1.21.11 {
    /*override fun getFabricId(): Identifier {
        return Searck.id("item_index")
    }
    *///?}
}