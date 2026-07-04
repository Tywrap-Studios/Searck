package org.tywrapstudios.searck.client.key

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import org.tywrapstudios.searck.Searck

@Environment(EnvType.CLIENT)
object SearckKeys {
    val CATEGORY = KeyMapping.Category.register(Searck.id("searck_category"))

    val OPEN_SEARCH = KeyMappingHelper.registerKeyMapping(
        KeyMapping(
            "key.searck.open_search",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            CATEGORY,
        )
    )

    val QUICK_ACTION = KeyMappingHelper.registerKeyMapping(
        KeyMapping(
            "key.searck.quick_action",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_ENTER,
            CATEGORY,
        )
    )

    fun register() {}
}