package org.tywrapstudios.searck.client.gui.screen

import com.github.terrakok.fuzzykot.extractTop
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import org.tywrapstudios.searck.Searck
import org.tywrapstudios.searck.client.key.SearckKeys
import org.tywrapstudios.searck.math.StringCalculator

@Environment(EnvType.CLIENT)
class SearchScreen : Screen(Component.translatable("gui.searck.search_screen.title")) {
    private lateinit var input: EditBox
    private lateinit var itemList: ItemList
    var lastValue = ""
    var lastSolution = 0.0

    val widgetWidth = 300
    val inputHeight = 20
    val widgetX get() = this.width / 2 - 150
    val inputY get() = this.height / 2 - 70

    override fun init() {
        input = EditBox(this.font, widgetX, inputY, widgetWidth, inputHeight, Component.empty())
        input.setMaxLength(2000)

        itemList = ItemList()
        itemList.x = widgetX

        addRenderableWidget(itemList)
        addRenderableWidget(input)

        setInitialFocus(input)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)

        graphics.text(this.font, Component.translatable("gui.searck.search_screen.title"), this.width / 2 - 150, this.height / 2 - 80, ARGB.white(1f))

        if (input.value != lastValue) {
            lastValue = input.value
            lastSolution = StringCalculator.calculate(input.value)
            itemList.update()
        }
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        if (this.minecraft.level == null) this.extractPanorama(graphics, a)
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (SearckKeys.PUSH_RESULT.matches(event)) {
            val selected =  itemList.selected
            if (selected is ItemList.CalculationEntry) {
                input.value = selected.solution
            } else if (selected is ItemList.ItemLikeEntry) {
                val player = minecraft.player!!
                val inv = player.inventory
                val searchItem = selected.itemLike.asItem().defaultInstance
                val matchingIndex = inv.findSlotMatchingItem(searchItem)
                if (matchingIndex == -1) return super.keyPressed(event)
                if (matchingIndex in 0..8) {
                    inv.selectedSlot = matchingIndex
                } else {
                    val slot = player.inventoryMenu.getSlot(matchingIndex)

                    val screen = InventoryScreen(player)

                    Searck.LOGGER.debug(
                        "Swapping {} ({}) to {} for {} ({}, {})",
                        slot,
                        slot.index,
                        inv.selectedSlot,
                        player,
                        inv.toList(),
                        player.inventoryMenu.slots.map { "$it (${it.index}, ${it.item})" }
                    )
                    screen.slotClicked(slot, slot.index, inv.selectedSlot, ContainerInput.SWAP)
                }
                minecraft.gui.setScreen(null)
            }
        }
        return super.keyPressed(event)
    }

    private inner class ItemList : ObjectSelectionList<ItemList.Entry>(
        this@SearchScreen.minecraft,
        this@SearchScreen.widgetWidth,
        this@SearchScreen.height - (this@SearchScreen.inputY + this@SearchScreen.inputHeight),
        this@SearchScreen.inputY + this@SearchScreen.inputHeight + 1,
        24
    ) {

        private fun populate() {
            if (!this@SearchScreen.lastSolution.isNaN()) {
                this.addEntry(CalculationEntry(this@SearchScreen.lastSolution.toString()))
            }

            val value = this@SearchScreen.input.value

            if (value.isEmpty()) {
                this.clearEntries()
                return
            }

            Searck.LOGGER.debug("Searching for: $value")
            val scoreGroups = mutableMapOf<Int, MutableList<String>>()
            Searck.activeIndex.getNames().extractTop(value, cutoff = 90).forEach {
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
                    Searck.activeIndex.getItemsForName(it).forEach { item ->
                        this.addEntry(ItemLikeEntry(item))
                    }
                }
            }

            if (this.children().isEmpty()) {
                this.addEntry(CalculationEntry("??"))
            }
        }

        fun update() {
            this.clearEntries()
            this.populate()
        }

        override fun getRowLeft() = this.x
        override fun getRowWidth() = if (scrollable()) this@SearchScreen.widgetWidth - this.scrollbarWidth()
        else this@SearchScreen.widgetWidth

        override fun scrollBarX() = this.rowRight

        abstract inner class Entry : ObjectSelectionList.Entry<Entry>()

        inner class CalculationEntry(val solution: String) : Entry() {
            override fun getNarration() =
                Component.translatable("gui.searck.search_screen.calc_entry.narration", solution)

            override fun extractContent(
                graphics: GuiGraphicsExtractor,
                mouseX: Int,
                mouseY: Int,
                hovered: Boolean,
                a: Float
            ) {
                graphics.text(
                    this@SearchScreen.font,
                    "=$solution",
                    contentX + 2,
                    contentY + contentHeight / 2 - this@SearchScreen.font.lineHeight / 2,
                    ARGB.white(1f)
                )
            }
        }

        inner class ItemLikeEntry(val itemLike: ItemLike) : Entry() {
            override fun getNarration(): Component {
                val stack = this.getStack()
                return if (!stack.isEmpty) Component.translatable(
                    "narrator.select",
                    stack.hoverName
                ) else CommonComponents.EMPTY
            }

            override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
                this@ItemList.selected = this
                return super.mouseClicked(event, doubleClick)
            }

            override fun extractContent(
                graphics: GuiGraphicsExtractor,
                mouseX: Int,
                mouseY: Int,
                hovered: Boolean,
                a: Float
            ) {
                if (minecraft.player!!.inventory.findSlotMatchingItem(this.getStack()) != -1) {
                    val color = if (this@ItemList.selected == this) ARGB.color(255, 232, 166)
                    else ARGB.color(255, 186, 0)
                    extractSelection(graphics, this, color)
                }
                val stack = this.getStack()
                this.blitSlot(graphics, contentX, contentY, stack)
                val y = this.contentYMiddle - 9 / 2
                graphics.text(
                    this@SearchScreen.font,
                    stack.hoverName,
                    this.contentX + 18 + 5,
                    y,
                    ARGB.white(1f)
                )
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
    }
}