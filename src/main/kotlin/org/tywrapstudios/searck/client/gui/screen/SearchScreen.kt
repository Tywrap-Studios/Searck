package org.tywrapstudios.searck.client.gui.screen

import com.github.terrakok.fuzzykot.extractTop
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ObjectSelectionList
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import org.tywrapstudios.searck.Searck
import org.tywrapstudios.searck.client.key.SearckKeys
import org.tywrapstudios.searck.compat.IngredientRole
import org.tywrapstudios.searck.compat.openViewer
import org.tywrapstudios.searck.config.InfoAction
import org.tywrapstudios.searck.config.SearckConfig
import org.tywrapstudios.searck.math.StringCalculator
import org.tywrapstudios.searck.platform.*
import org.tywrapstudios.searck.search.ItemIndex

//? >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.inventory.ContainerInput
//?} else {
//?}

//? >=1.21.11 {
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
//?}

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

    //? >=26.1 {
    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
    //?} else {
    /*override fun render(graphics: GuiGraphicsExtractor, i: Int, j: Int, f: Float) {
        super.render(graphics, i, j, f)
        *///?}

        graphics.text(this.font, Component.translatable("gui.searck.search_screen.title"), this.width / 2 - 150, this.height / 2 - 80, ARGB.white(1f))

        if (input.value != lastValue) {
            lastValue = input.value
            lastSolution = StringCalculator.calculate(input.value)
            itemList.update()
        }
    }

    //? >=26.1 {
    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
    //?} else {
    /*override fun renderBackground(graphics: GuiGraphicsExtractor, i: Int, j: Int, a: Float) {
        super.renderBackground(graphics, i, j, a)
    *///?}
        //? <1.21.11
        //val minecraft = this.minecraft!!
        if (minecraft.level == null) {
            //? >=26.1 {
            this.extractPanorama(graphics, a)
            //?} else {
            /*this.renderPanorama(graphics, a)
            *///?}
        }
    }

    //? >=1.21.11 {
    override fun keyPressed(event: KeyEvent): Boolean {
    //?} else {
    /*override fun keyPressed(i: Int, j: Int, k: Int): Boolean {
    *///?}

        //? <1.21.11
        //val minecraft = this.minecraft!!
        //? >=1.21.11 {
        if (SearckKeys.QUICK_ACTION.matches(event)) {
        //?} else {
        /*if (SearckKeys.QUICK_ACTION.matches(i, j)) {
        *///?}
            val selected =  itemList.selected
            if (selected is ItemList.CalculationEntry) {
                input.value = selected.solution
            } else if (selected is ItemList.ItemLikeEntry) {
                val player = minecraft.player!!
                val inv = player.inventory
                val searchItem = selected.itemLike.asItem().defaultInstance
                val matchingIndex = inv.findSlotMatchingItem(searchItem)
                if (matchingIndex == -1) {
                    when (SearckConfig.infoAction) {
                        InfoAction.OPEN_IN -> openViewer(IngredientRole.INPUT, searchItem, minecraft, this)
                        InfoAction.OPEN_OUT -> openViewer(IngredientRole.OUTPUT, searchItem, minecraft, this)
                        else -> minecraft.vUtil.setScreen(InfoScreen(selected.itemLike, this))
                    }
                    //? >=1.21.11 {
                    return super.keyPressed(event)
                    //?} else {
                    /*return super.keyPressed(i, j, k)
                    *///?}
                }
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
                        //? >=1.21.11 {
                        inv.toList(),
                        //?} else {
                        /*inv.items,
                        *///?}
                        player.inventoryMenu.slots.map { "$it (${it.index}, ${it.item})" }
                    )
                    screen.slotClicked(slot, slot.index, inv.selectedSlot, ContainerInput.SWAP)
                }
                minecraft.vUtil.setScreen(null)
            }
        }
        //? >=1.21.11 {
        return super.keyPressed(event)
        //?} else {
        /*return super.keyPressed(i, j, k)
        *///?}
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
            ItemIndex.getActive().getNames().extractTop(value, cutoff = SearckConfig.cutoff).forEach {
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
                    ItemIndex.getActive().getItemsForName(it).forEach { item ->
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
        //? >=26.1 {
        override fun getRowWidth() = if (scrollable()) this@SearchScreen.widgetWidth - this.scrollbarWidth()
        //?} else {
        /*override fun getRowWidth() = if (scrollbarVisible()) this@SearchScreen.widgetWidth - 6
        *///?}
        else this@SearchScreen.widgetWidth

        //? >=1.21.11 {
        override fun scrollBarX() = this.rowRight
        //?} else {
        /*override fun getScrollbarPosition() = this.rowRight
        *///?}

        abstract inner class Entry : ObjectSelectionList.Entry<Entry>()

        inner class CalculationEntry(val solution: String) : Entry() {
            override fun getNarration() =
                Component.translatable("gui.searck.search_screen.calc_entry.narration", solution)

            //? >=26.1 {
            override fun extractContent(
            //?} else if >=1.21.11 {
            /*override fun renderContent(
            *///?} else {
            /*override fun render(
            *///?}
                graphics: GuiGraphicsExtractor,
                index: Int,
                top: Int,
                //? <=1.21.1 {
                /*left: Int,
                width: Int,
                height: Int,
                mouseX: Int,
                mouseY: Int,
                *///?}
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

            //? >=1.21.11 {
            override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        //?} else {
            /*override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
                *///?}
                this@ItemList.selected = this
                //? >=1.21.11 {
                return super.mouseClicked(event, doubleClick)
                //?} else {
                /*return super.mouseClicked(d, e, i)
                *///?}
            }

            //? >=26.1 {
            override fun extractContent(
            //?} else if >=1.21.11 {
            /*override fun renderContent(
            *///?} else {
            /*override fun render(
                *///?}
                graphics: GuiGraphicsExtractor,
                index: Int,
                top: Int,
                //? <=1.21.1 {
                /*left: Int,
                width: Int,
                height: Int,
                mouseX: Int,
                mouseY: Int,
                *///?}
                hovered: Boolean,
                a: Float
            ) {
                //? <=1.21.11 {
                /*val contentHeight = height
                *///?}
                if (minecraft.player!!.inventory.findSlotMatchingItem(this.getStack()) != -1) {
                    val color = if (this@ItemList.selected == this) ARGB.color(255, 232, 166)
                    else ARGB.color(255, 186, 0)
                    //? >=26.1 {
                    extractSelection(graphics, this, color)
                    //?} else if >=1.21.11 {
                    /*renderSelection(graphics, this, color)
                    *///?} else {
                    /*val p = if (this.isFocused) -1 else -8355712
                    renderSelection(graphics, contentHeight, contentXMiddle, contentYMiddle, p, color)
                    *///?}
                }
                val stack = this.getStack()
                this.blitSlot(graphics, contentX, contentY, stack)
                val y = contentYMiddle - 9 / 2
                graphics.text(
                    this@SearchScreen.font,
                    stack.hoverName,
                    contentX + 18 + 5,
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
                //? >=1.21.11 {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 18, 18)
                //?} else {
                /*graphics.blitSprite(sprite, x, y, 0, 18, 18)
                *///?}
            }
        }
    }
}