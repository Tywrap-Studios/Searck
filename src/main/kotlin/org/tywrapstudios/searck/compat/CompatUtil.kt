package org.tywrapstudios.searck.compat

import me.shedaniel.rei.api.client.view.ViewSearchBuilder
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.client.gui.screen.InfoScreen
import org.tywrapstudios.searck.compat.rei.SearckRei
import org.tywrapstudios.searck.platform.vUtil

//? >1.21.9 || <1.21.2 {
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.recipe.RecipeIngredientRole
import org.tywrapstudios.searck.compat.jei.SearckJei
//?}

//? <=1.21.1 {
/*import dev.emi.emi.api.EmiApi
import dev.emi.emi.api.stack.EmiIngredient
import org.tywrapstudios.searck.compat.emi.SearckEmi
import net.minecraft.world.item.crafting.Ingredient
*///?}

fun getActiveViewer(): ActiveViewer {

    fun check(mod: String) = FabricLoader.getInstance().isModLoaded(mod)

    if (check("roughlyenoughitems")) return ActiveViewer.REI
    if (check("emi")) return ActiveViewer.EMI
    if (check("jei")) return ActiveViewer.JEI

    return ActiveViewer.NONE
}

fun containsEntries(role: IngredientRole, stack: ItemStack): Boolean {
    return when (getActiveViewer()) {
        //? >1.21.9 || <1.21.2
        ActiveViewer.JEI -> SearckJei.containsEntries(role, stack)
        //? <=1.21.1
        //ActiveViewer.EMI -> SearckEmi.containsEntries(role, stack)
        ActiveViewer.REI -> SearckRei.containsEntries(role, stack)
        else -> false
    }
}

fun openViewer(role: IngredientRole, stack: ItemStack, minecraft: Minecraft, parent: Screen) {
    val active = getActiveViewer()
    when (active) {
        //? >1.21.9 || <1.21.2 {
        ActiveViewer.JEI -> {
            val jeiRole = if (role == IngredientRole.INPUT) RecipeIngredientRole.INPUT else RecipeIngredientRole.OUTPUT
            val focus = SearckJei.runtime.jeiHelpers.focusFactory.createFocus(
                jeiRole, VanillaTypes.ITEM_STACK, stack
            )
            SearckJei.runtime.recipesGui.show(focus)
        }
        //?}
        //? <=1.21.1 {
        /*ActiveViewer.EMI -> {
            val ingredient = EmiIngredient.of(Ingredient.of(stack))
            when (role) {
                IngredientRole.OUTPUT -> EmiApi.displayRecipes(ingredient)
                IngredientRole.INPUT -> EmiApi.displayUses(ingredient)
            }
        }
        *///?}
        ActiveViewer.REI -> {
            when (role) {
                IngredientRole.OUTPUT -> ViewSearchBuilder.builder()
                    .addRecipesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack))
                    .open()
                IngredientRole.INPUT -> ViewSearchBuilder.builder()
                    .addUsagesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack))
                    .open()
            }
        }
        else -> {
            minecraft.vUtil.setScreen(InfoScreen(stack.item, parent))
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

interface RecipeViewer {
    fun containsEntries(role: IngredientRole, stack: ItemStack): Boolean
}