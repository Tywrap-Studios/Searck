package org.tywrapstudios.searck.client.gui.screen

import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.recipe.RecipeIngredientRole
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
import org.tywrapstudios.searck.compat.ActiveViewer
import org.tywrapstudios.searck.compat.getActiveViewer
import org.tywrapstudios.searck.compat.jei.SearckJei

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
            val focus = SearckJei.runtime.jeiHelpers.focusFactory.createFocus(
                    RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, getStack()
                )
            SearckJei.runtime
                .recipesGui
                .show(focus)
        }.bounds(widgetX - 140 / 2 - 10, height - 60, 140, 20).build()
        openOutputViewer.active = getActiveViewer() != ActiveViewer.NONE
        addRenderableWidget(openOutputViewer)
        openInputViewer = Button.builder(
            Component.translatable("gui.searck.info_screen.button.open_input")
        ) { _ ->
            val focus = SearckJei.runtime.jeiHelpers.focusFactory.createFocus(
                RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, getStack()
            )
            SearckJei.runtime
                .recipesGui
                .show(focus)
        }.bounds(widgetX + 140 / 2 + 10, height - 60, 140, 20).build()
        openInputViewer.active = getActiveViewer() != ActiveViewer.NONE
        addRenderableWidget(openInputViewer)
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