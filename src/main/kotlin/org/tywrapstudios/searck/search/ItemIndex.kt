package org.tywrapstudios.searck.search

import net.minecraft.client.resources.language.ClientLanguage
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.packs.resources.ReloadableResourceManager
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.Searck
import org.tywrapstudios.searck.Searck.client

object ItemIndex : ResourceManagerReloadListener {
    private var initialized = false

    val cache = mutableMapOf<String, Pair<String, ItemStack>>()

    internal fun index() {
        cache.clear()

        val lang = ClientLanguage.getInstance()
        if (lang is ClientLanguage) {
            lang.storage.filter {
                it.key.matches("(block|item)\\.([a-z_-]+)\\.([a-z_-]+)$".toRegex())
            }.forEach { (key, translation) ->
                val groups = "(block|item)\\.([a-z_-]+)\\.([a-z_-]+)$".toRegex()
                    .find(key)!!
                    .groupValues
                val isItem = groups[1] == "item"
                val namespace = groups[2]
                val path = groups[3]
                val item = ItemStack(if (isItem) {
                    BuiltInRegistries.ITEM.getValue(Searck.id(namespace, path))
                } else {
                    BuiltInRegistries.BLOCK.getValue(Searck.id(namespace, path)).asItem()
//                    BuiltInRegistries.ITEM.getValue(Searck.id(namespace, path)) as BlockItem
                })
                Searck.LOGGER.debug("Caching:")
                Searck.LOGGER.debug("   isItem: $isItem")
                Searck.LOGGER.debug("   ID: $namespace:$path")
                Searck.LOGGER.debug("   given: $translation")
                Searck.LOGGER.debug("   shown: ${item.styledHoverName.string}")
                Searck.LOGGER.debug("Cached item {}", item)
                cache[key] = translation to item
            }
        } else {
            Searck.LOGGER.warn("Could not fetch ClientLanguage. Please run /searck index manually")
        }
    }

    fun indexIfNotInitialized() {
        Searck.LOGGER.info("Requested index, initialisation needed: ${!initialized}")
        if (!initialized) {
            Searck.LOGGER.info("Initializing indexing")
            (client.resourceManager as ReloadableResourceManager)
                .registerReloadListener(ItemIndex)
            index()
            initialized = true
        }
    }

    fun getItemsForName(name: String): List<ItemStack> = cache.filter {
        it.value.first == name
    }.values.map { it.second }

    fun getNames() = cache.values.map { it.first }

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        Searck.LOGGER.info("Reloaded: Indexing items...")
        index()
    }
}