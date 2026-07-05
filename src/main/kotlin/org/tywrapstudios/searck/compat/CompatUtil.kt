package org.tywrapstudios.searck.compat

import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.recipe.RecipeIngredientRole
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.client.gui.screen.InfoScreen
import org.tywrapstudios.searck.compat.jei.SearckJei

fun getActiveViewer(): ActiveViewer {

    fun check(mod: String) = FabricLoader.getInstance().isModLoaded(mod)

    if (check("rei")) return ActiveViewer.REI
    if (check("emi")) return ActiveViewer.EMI
    if (check("jei")) return ActiveViewer.JEI

    return ActiveViewer.NONE
}

fun openViewer(role: IngredientRole, stack: ItemStack, minecraft: Minecraft, parent: Screen) {
    val active = getActiveViewer()
    when (active) {
        ActiveViewer.JEI -> {
            val jeiRole = if (role == IngredientRole.INPUT) RecipeIngredientRole.INPUT else RecipeIngredientRole.OUTPUT
            val focus = SearckJei.runtime.jeiHelpers.focusFactory.createFocus(
                jeiRole, VanillaTypes.ITEM_STACK, stack
            )
            SearckJei.runtime.recipesGui.show(focus)
        }
        ActiveViewer.EMI -> {
            TODO("EMI integration")
        }
        ActiveViewer.REI -> {
            TODO("REI integration")
        }
        else -> {
            minecraft.gui.setScreen(InfoScreen(stack.item, parent))
        }
    }
}

enum class ActiveViewer {
    NONE,
    JEI,
    EMI,
    REI
}

enum class IngredientRole {
    INPUT,
    OUTPUT
}