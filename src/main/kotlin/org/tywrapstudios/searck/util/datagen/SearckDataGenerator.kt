package org.tywrapstudios.searck.util.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.tywrapstudios.searck.util.datagen.provider.SearckLangProvider
import org.tywrapstudios.searck.util.datagen.provider.SearckNlNlLangProvider

object SearckDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = fabricDataGenerator.createPack()

        pack.addProvider(::SearckLangProvider)
        pack.addProvider(::SearckNlNlLangProvider)
    }
}