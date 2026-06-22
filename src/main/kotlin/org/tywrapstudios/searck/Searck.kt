package org.tywrapstudios.searck

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.searck.client.gui.screen.SearchScreen
import org.tywrapstudios.searck.client.key.SearckKeys

object Searck : ClientModInitializer {
    const val MOD_ID = /*$ mod_id*/ "searck"
    val LOGGER: Logger = LoggerFactory.getLogger("Searck")
    val client get() = Minecraft.getInstance()

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

    fun id(path: String) = id(MOD_ID, path)

    fun id(namespace: String, path: String): Identifier {
        //? if <1.21 {
        /*return Identifier(namespace, path)
        *///?} else
        return Identifier.fromNamespaceAndPath(namespace, path)
    }
}