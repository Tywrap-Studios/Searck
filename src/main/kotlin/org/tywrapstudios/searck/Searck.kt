package org.tywrapstudios.searck

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.resources.Identifier
import org.slf4j.LoggerFactory
import org.tywrapstudios.searck.client.gui.screen.SearchScreen
import org.tywrapstudios.searck.client.key.SearckKeys

object Searck : ClientModInitializer {
    val MOD_ID = /*$ mod_id*/ "searck"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitializeClient() {
        SearckKeys.register()

        events()
    }

    private fun events() {
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (SearckKeys.OPEN_SEARCH.isDown) {
                if (client.gui.screen() is SearchScreen) {
                    client.gui.setScreen(null)
                } else {
                    client.gui.setScreen(SearchScreen())
                }
            }
        }
    }

    fun id(path: String): Identifier {
        //? if <1.21 {
        /*return Identifier(MOD_ID, path)
        *///?} else
        return Identifier.fromNamespaceAndPath(MOD_ID, path)
    }
}