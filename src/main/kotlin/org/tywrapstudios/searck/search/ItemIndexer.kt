package org.tywrapstudios.searck.search

import net.minecraft.world.level.ItemLike

/**
 * Interface that can be used to make different "indexers" for use during search.
 *
 * Implementation has to be a class with an empty constructor. It
 * cannot be an `object` or have parameters, because this will break the reflection
 * used in [ItemIndex.getActive]. (This does not matter for built-in indexers, given
 * the logic can be fine-tuned)
 */
interface ItemIndexer {
    /**
     * Run logic to "index" items to names (or vice versa, just make sure you can
     * properly implement [getNames] and [getItemsForName]).
     *
     * This gets called when Minecraft resources are reloaded (for the language)
     * or if the indexer is fetched using [ItemIndex.getActive] and has not yet
     * been indexed at least once. It can be run at any arbitrary time during the
     * lifecycle though, so beware of that in your implementation.
     *
     * When this indexer is the selected indexer in config, this method is guaranteed to be called
     * twice during initial resources load. Once because it's technically not yet been indexed, and then once
     * because the resource manager has also technically been reloaded.
     *
     * @see LangIndexer.index
     * @see RegistryIndexer.index
     */
    fun index()

    /**
     * Returns a list of all the names of the indexed [ItemLike]s.
     */
    fun getNames(): List<String>

    /**
     * Returns a list of [ItemLike]s which name match the name given.
     * @param name Name of the [ItemLike](s) searched for
     */
    fun getItemsForName(name: String): List<ItemLike>
}