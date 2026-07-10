package org.tywrapstudios.searck.platform

//? <26.1 {
/*import net.minecraft.world.inventory.ClickType
import net.minecraft.world.item.ItemStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
*///?}

//? >=1.21.11 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.server.packs.resources.PreparableReloadListener
//?} else {
/*import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.core.DefaultedRegistry
import net.minecraft.server.packs.resources.PreparableReloadListener
*///?}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

class MinecraftVersionUtil(val minecraft: Minecraft) {
    fun getScreen(): Screen? {
        //? if <26.2 {
        /*return minecraft.screen
        *///?} else {
        return minecraft.gui.screen()
        //?}
    }

    fun setScreen(screen: Screen?) {
        //? if <26.2 {
        /*minecraft.setScreen(screen)
        *///?} else {
        minecraft.gui.setScreen(screen)
        //?}
    }
}

val Minecraft?.vUtil: MinecraftVersionUtil
    get() = MinecraftVersionUtil(this ?: Minecraft.getInstance())

//? <26.1 {
/*typealias GuiGraphicsExtractor = GuiGraphics
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

//? >=1.21.11 {
fun ResourceLoader.registerReloadListener(id: Identifier, reloader: PreparableReloadListener) {
    this.registerReloader(id, reloader)
}
//?} else {
/*fun GuiGraphicsExtractor.setTooltipForNextFrame(font: Font, stack: ItemStack, i: Int, j: Int) {
    this.renderTooltip(font, stack, i, j)
}

object ARGB {
    fun white(a: Float): Int {
        return 16777215
    }

    fun color(r: Int, g: Int, b: Int): Int {
        //(255 and 255) shl 24 or (r and 255) shl 16 or (g and 255) shl 8 or b and 255
        return (255 shl 24) or (r shl 16) or (g shl 8) or b
    }
}

var Inventory.selectedSlot: Int
    set(value) {
        this.selected = value
    }
    get() = this.selected

typealias ResourceLoader = ResourceManagerHelper

fun ResourceLoader.registerReloadListener(id: Identifier, reloader: IdentifiableResourceReloadListener) {
    this.registerReloadListener(reloader)
}


//? <1.21.2 {
/*fun <T> DefaultedRegistry<T>.getValue(id: Identifier): T {
    return this.get(id)
}
*///?}

*///?}

*///?}