package org.tywrapstudios.searck.search

import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.client.resources.language.ClientLanguage
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.level.ItemLike
import org.tywrapstudios.searck.Searck

object RegistryIndexer : ItemIndexer {
    val cache = mutableMapOf<Identifier, Pair<String, ItemLike>>()

    override fun index() {
        cache.clear()

        val lang = ClientLanguage.getInstance()

        BuiltInRegistries.ITEM.asHolderIdMap().forEach { holder ->
            val key = holder.unwrapKey().get().identifier()
            val item = holder.value()
            val name = try {
                item.defaultInstance.itemName.string
            } catch (_: Exception) {
                if (lang is ClientLanguage) {
                    lang.storage[item.descriptionId] ?: item.descriptionId
                } else {
                    item.descriptionId
                }
            }
            Searck.LOGGER.debug("Caching: {}, called {}", key, name)
            Searck.LOGGER.debug("Cached item {}", item)
            cache[key] = name to item
        }
        BuiltInRegistries.BLOCK.asHolderIdMap().forEach { holder ->
            val key = holder.unwrapKey().get().identifier()
            val block = holder.value()
            val item = block.asItem()
            val name = try {
                item.defaultInstance.itemName.string
            } catch (_: Exception) {
                if (lang is ClientLanguage) {
                    lang.storage[block.descriptionId] ?: block.descriptionId
                } else {
                    block.descriptionId
                }
            }
            Searck.LOGGER.debug("Caching: {}, called {}", key, name)
            Searck.LOGGER.debug("Cached block {} as {}", block, item)
            cache[key] = name to block
        }
    }

    override fun getNames(): List<String> = cache.values.map { it.first }

    override fun getItemsForName(name: String): List<ItemLike> = cache.filter {
        it.value.first == name
    }.values.map { it.second }

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        Searck.LOGGER.info("Reloaded: Indexing items...")
        index()
    }

    fun register() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(Searck.id("registry_indexer"), RegistryIndexer)
    }
}