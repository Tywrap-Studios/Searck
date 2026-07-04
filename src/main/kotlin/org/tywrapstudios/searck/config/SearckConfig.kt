package org.tywrapstudios.searck.config

import eu.midnightdust.lib.config.MidnightConfig
import net.minecraft.util.StringRepresentable

class SearckConfig : MidnightConfig() {
    companion object {
        const val SEARCH = "search"

        @Entry(category = SEARCH) @JvmField
        var indexer: IndexerOption = IndexerOption.REGISTRY

        @Condition(requiredOption = "searck:indexer", requiredValue = ["CUSTOM"])
        @Entry(category = SEARCH) @JvmField
        var customIndexer: String = ""

        @Entry(category = SEARCH, min = 0.0, max = 100.0, isSlider = true) @JvmField
        var cutoff: Int = 90

        const val KEYS = "keys"

    }
}

enum class IndexerOption(val key: String) : StringRepresentable {
    REGISTRY("option.indexer.registry"),
    LANG_FILE("option.indexer.lang_file"),
    CUSTOM("option.indexer.custom");

    override fun getSerializedName() = key
}
