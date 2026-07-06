package org.tywrapstudios.searck.client.gui.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import org.tywrapstudios.searck.compat.IngredientRole
import org.tywrapstudios.searck.compat.containsEntries
import org.tywrapstudios.searck.compat.openViewer
import org.tywrapstudios.searck.platform.vUtil
//? if >=26.1 {
//import net.minecraft.client.gui.GuiGraphicsExtractor
//?} else {
import org.tywrapstudios.searck.platform.GuiGraphicsExtractor
import org.tywrapstudios.searck.platform.fakeItem
import org.tywrapstudios.searck.platform.text
//?}

@Environment(EnvType.CLIENT)
class InfoScreen(val itemLike: ItemLike, val parent: Screen) : Screen(Component.translatable("gui.searck.info_screen.title")) {
    lateinit var openOutputViewer: Button
    lateinit var openInputViewer: Button

    val widgetX get() = width / 2 - 70
    val widgetY get() = height / 2 - 70

    override fun init() {
        openOutputViewer = Button.builder(
            Component.translatable("gui.searck.info_screen.button.open_output")
        ) { _ ->
            openViewer(IngredientRole.OUTPUT, getStack(), minecraft, this)
        }.bounds(widgetX - 140 / 2 - 10, height - 60, 140, 20).build()
        openOutputViewer.active = containsEntries(IngredientRole.OUTPUT, getStack())
        addRenderableWidget(openOutputViewer)

        openInputViewer = Button.builder(
            Component.translatable("gui.searck.info_screen.button.open_input")
        ) { _ ->
            openViewer(IngredientRole.INPUT, getStack(), minecraft, this)
        }.bounds(widgetX + 140 / 2 + 10, height - 60, 140, 20).build()
        openInputViewer.active = containsEntries(IngredientRole.INPUT, getStack())
        addRenderableWidget(openInputViewer)
    }

    //? >=26.1 {
//    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
//        super.extractRenderState(graphics, mouseX, mouseY, a)
    //?} else {
    override fun render(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        super.render(graphics, i, j, f)
    //?}

        val stack = this.getStack()

        this.blitSlot(graphics, widgetX, widgetY, stack)

        graphics.text(
            this.font,
            stack.hoverName,
            widgetX + 18 + 5,
            widgetY + 10 - this.font.lineHeight / 2,
            ARGB.white(1f)
        )

        graphics.setTooltipForNextFrame(
            this.font,
            stack,
            widgetX - 8,
            widgetY + 38
        )
    }

    override fun onClose() {
        this.minecraft.vUtil.setScreen(parent)
    }

    private fun getStack() = ItemStack(this.itemLike)

    private fun blitSlot(graphics: GuiGraphicsExtractor, x: Int, y: Int, stack: ItemStack) {
        blitSlotBg(graphics, x + 1, y + 1)
        if (!stack.isEmpty) {
            graphics.fakeItem(stack, x + 2, y + 2)
        }
    }

    private fun blitSlotBg(graphics: GuiGraphicsExtractor, x: Int, y: Int) {
        val sprite = Identifier.withDefaultNamespace("container/slot")
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 18, 18)
    }
}