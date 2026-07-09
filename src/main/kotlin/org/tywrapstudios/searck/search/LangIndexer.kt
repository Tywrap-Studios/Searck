package org.tywrapstudios.searck.search

import net.minecraft.client.resources.language.ClientLanguage
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.ItemLike
import org.tywrapstudios.searck.Searck
//? <1.21.2
//import org.tywrapstudios.searck.platform.getValue

object LangIndexer : ItemIndexer {
    val cache = mutableMapOf<String, Pair<String, ItemLike>>()

    override fun index() {
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
                val id = Searck.id(namespace, path)
                val item: ItemLike = if (isItem) {
                    BuiltInRegistries.ITEM.getValue(id)
                } else {
                    BuiltInRegistries.BLOCK.getValue(id)
                }
                Searck.LOGGER.debug("Caching:")
                Searck.LOGGER.debug("   isItem: $isItem")
                Searck.LOGGER.debug("   ID: $namespace:$path")
                Searck.LOGGER.debug("   given: $translation")
                Searck.LOGGER.debug("Cached item {}", item)
                cache[key] = translation to item
            }
        } else {
            Searck.LOGGER.warn("Could not fetch ClientLanguage.")
        }
    }

    override fun getItemsForName(name: String): List<ItemLike> = cache.filter {
        it.value.first == name
    }.values.map { it.second }

    override fun getNames() = cache.values.map { it.first }
}