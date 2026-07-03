package org.tywrapstudios.searck.search

import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.world.level.ItemLike

interface ItemIndexer : ResourceManagerReloadListener {
    fun index()
    fun getNames(): List<String>
    fun getItemsForName(name: String): List<ItemLike>
}