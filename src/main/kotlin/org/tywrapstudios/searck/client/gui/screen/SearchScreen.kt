package org.tywrapstudios.searck.client.gui.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import org.tywrapstudios.searck.client.key.SearckKeys
import org.tywrapstudios.searck.math.StringCalculator

@Environment(EnvType.CLIENT)
class SearchScreen : Screen(Component.translatable("gui.searck.search_screen.title")) {
    lateinit var input: EditBox
    var lastCalc = ""
    var lastSolution = 0.0

    override fun init() {
        input = EditBox(this.font, this.width / 2 - 150, this.height / 2 - 70, 300, 20, Component.empty())
        input.setMaxLength(2000)
        addRenderableWidget(input)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        val calc = input.value
        val output = if (calc != lastCalc) {
            lastCalc = calc
            val solution = StringCalculator.calculate(calc)
            lastSolution = solution
            if (solution.isNaN()) "???" else solution.toString()
        } else if (lastSolution.isNaN()) "???" else lastSolution.toString()

        if (!calc.isEmpty()) graphics.setComponentTooltipForNextFrame(this.font, listOf(Component.literal(output)), mouseX, mouseY)
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        if (this.minecraft.level == null) this.extractPanorama(graphics, a)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (SearckKeys.PUSH_SOLUTION.matches(event)) {
            input.value = lastSolution.toString()
        }
        return super.keyPressed(event)
    }
}