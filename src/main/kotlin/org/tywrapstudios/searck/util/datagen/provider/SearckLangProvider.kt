package org.tywrapstudios.searck.util.datagen.provider

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class SearckLangProvider(dataOutput: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    override fun generateTranslations(
        registryLookup: HolderLookup.Provider,
        builder: TranslationBuilder
    ) {
        builder.add("gui.searck.search_screen.title", "Quick Search")
        builder.add("gui.searck.search_screen.calc_entry.narration", "Answer is %s")
    }
}

class SearckNlNlLangProvider(dataOutput: FabricPackOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, "nl_nl", registryLookup) {

    override fun generateTranslations(
        registryLookup: HolderLookup.Provider,
        builder: TranslationBuilder
    ) {
        builder.add("gui.searck.search_screen.title", "Snel Opzoeken")
        builder.add("gui.searck.search_screen.calc_entry.narration", "Uitkomst is %s")
    }
}