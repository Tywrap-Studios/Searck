package org.tywrapstudios.searck.platform

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

class VersionUtil(val minecraft: Minecraft) {
    fun getScreen(): Screen? {
        //? if <26.2 {
        return minecraft.screen
        //?} else {
        //return minecraft.gui.screen()
        //?}
    }

    fun setScreen(screen: Screen?) {
        //? if <26.2 {
        minecraft.setScreen(screen)
        //?} else {
        //minecraft.gui.setScreen(screen)
        //?}
    }
}

val Minecraft.vUtil: VersionUtil
    get() = VersionUtil(this)