package org.tywrapstudios.searck.client.gui.screen

import com.github.terrakok.fuzzykot.extractTop
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.world.item.Item
import org.tywrapstudios.searck.Searck
import org.tywrapstudios.searck.client.key.SearckKeys
import org.tywrapstudios.searck.math.StringCalculator
import org.tywrapstudios.searck.search.ItemIndex

@Environment(EnvType.CLIENT)
class SearchScreen : Screen(Component.translatable("gui.searck.search_screen.title")) {
    lateinit var input: EditBox
    var lastValue = ""
    var lastSolution = 0.0
    val tooltipList = mutableListOf<Component>()
    val itemList = mutableListOf<Item>()

    override fun init() {
        ItemIndex.indexIfNotInitialized()

        input = EditBox(this.font, this.width / 2 - 150, this.height / 2 - 70, 300, 20, Component.empty())
        input.setMaxLength(2000)
        addRenderableWidget(input)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)

        graphics.text(this.font, Component.translatable("gui.searck.search_screen.title"), this.width / 2 - 150, this.height / 2 - 80, ARGB.white(255))

        val value = input.value
        if (value.isEmpty()) return

        if (value != lastValue) {
            tooltipList.clear()
            itemList.clear()
            lastValue = value
            val solution = StringCalculator.calculate(value)
            lastSolution = solution

            if (lastSolution.isNaN()) {
                Searck.LOGGER.debug("Searching for: $value")
                val scoreGroups = mutableMapOf<Int, MutableList<String>>()
                ItemIndex.getNames().extractTop(value, cutoff = 90).forEach {
                    Searck.LOGGER.debug("Extracted: {}", it)
                    if (scoreGroups[it.score] == null) {
                        scoreGroups[it.score] = mutableListOf()
                    }
                    scoreGroups[it.score]!!.add(it.referent)
                }
                scoreGroups.forEach { (score, group) ->
                    Searck.LOGGER.debug("Group with score {}: {}", score, group)
                    group.sort()
                    Searck.LOGGER.debug("After sort: {}", group)
                    group.forEach {
                        ItemIndex.getItemsForName(it).forEach { item ->
                            if (item.defaultInstance.isEmpty) {
                                tooltipList.add(Component.literal(it))
                            } else {
                                tooltipList.add(item.defaultInstance.styledHoverName)
                            }
                            itemList.add(item)
                        }
                    }
                }
            } else {
                tooltipList.add(Component.literal(lastSolution.toString()))
            }
        }

        if (tooltipList.isEmpty()) {
            tooltipList.addFirst(Component.literal("=??"))
        }

        graphics.setComponentTooltipForNextFrame(this.font, tooltipList, mouseX, mouseY)
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        if (this.minecraft.level == null) this.extractPanorama(graphics, a)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (SearckKeys.PUSH_SOLUTION.matches(event) && !lastSolution.isNaN()) {
            input.value = lastSolution.toString()
        }
        return super.keyPressed(event)
    }
}