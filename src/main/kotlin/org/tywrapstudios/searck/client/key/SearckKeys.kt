package org.tywrapstudios.searck.client.key

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
//? >=26.1 {
//import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
//?} else {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
//?}
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW
import org.tywrapstudios.searck.Searck

@Environment(EnvType.CLIENT)
object SearckKeys {
    val CATEGORY = KeyMapping.Category.register(Searck.id("searck_category"))

    //? >=26.1 {
//    val OPEN_SEARCH = KeyMappingHelper.registerKeyMapping(
    //?} else {
    val OPEN_SEARCH = KeyBindingHelper.registerKeyBinding(
    //?}
        KeyMapping(
            "key.searck.open_search",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            CATEGORY,
        )
    )

    //? >=26.1 {
//    val QUICK_ACTION = KeyMappingHelper.registerKeyMapping(
    //?} else {
    val QUICK_ACTION = KeyBindingHelper.registerKeyBinding(
        //?}
        KeyMapping(
            "key.searck.quick_action",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_ENTER,
            CATEGORY,
        )
    )

    fun register() {}
}