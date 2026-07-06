package org.tywrapstudios.searck.platform

import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack

class MinecraftVersionUtil(val minecraft: Minecraft) {
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

val Minecraft.vUtil: MinecraftVersionUtil
    get() = MinecraftVersionUtil(this)

//? <26.1 {
typealias GuiGraphicsExtractor = GuiGraphics
typealias ContainerInput = ClickType

fun GuiGraphicsExtractor.text(font: Font, component: Component, i: Int, j: Int, k: Int) {
    this.drawString(font, component, i, j ,k)
}

fun GuiGraphicsExtractor.text(font: Font, string: String, i: Int, j: Int, k: Int) {
    this.drawString(font, string, i, j ,k)
}

fun GuiGraphicsExtractor.fakeItem(itemStack: ItemStack, i: Int, j: Int) {
    this.renderFakeItem(itemStack, i, j)
}

fun ResourceLoader.registerReloadListener(id: Identifier, reloader: PreparableReloadListener) {
    this.registerReloader(id, reloader)
}

//?}