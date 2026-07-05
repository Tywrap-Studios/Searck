package org.tywrapstudios.searck.client.gui.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

@Environment(EnvType.CLIENT)
class InfoScreen(val itemLike: ItemLike, val parent: Screen) : Screen(Component.translatable("gui.searck.info_screen.title")) {
    lateinit var openRecipeViewer: Button

    val widgetX get() = width / 2 - 70
    val widgetY get() = height / 2 - 70

    override fun init() {
        openRecipeViewer = Button.builder(
            Component.translatable("gui.searck.info_screen.button.open_viewer")
        ) {
            TODO("Open JEI/EMI/REI")
        }.bounds(widgetX, height - 60, 140, 20).build()

        addRenderableWidget(openRecipeViewer)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)

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
        this.minecraft.gui.setScreen(parent)
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