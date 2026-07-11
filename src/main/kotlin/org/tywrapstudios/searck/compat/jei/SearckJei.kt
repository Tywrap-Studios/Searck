//? >1.21.9 || <1.21.2 {
package org.tywrapstudios.searck.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.Searck
import org.tywrapstudios.searck.compat.IngredientRole
import org.tywrapstudios.searck.compat.RecipeViewer

@JeiPlugin
object SearckJei : IModPlugin, RecipeViewer {
    lateinit var runtime: IJeiRuntime

    override fun onRuntimeAvailable(runtime: IJeiRuntime) {
        this.runtime = runtime
    }

    override fun getPluginUid(): Identifier {
        return Searck.id("jei_plugin")
    }

    override fun containsEntries(
        role: IngredientRole,
        stack: ItemStack
    ): Boolean {
        val jeiRole = if (role == IngredientRole.INPUT) RecipeIngredientRole.INPUT else RecipeIngredientRole.OUTPUT
        val focus = runtime.jeiHelpers.focusFactory.createFocus(
            jeiRole, VanillaTypes.ITEM_STACK, stack
        )
        return runtime.recipeManager.createRecipeCategoryLookup()
            .limitFocus(listOf(focus))
            .get()
            .toList()
            .isNotEmpty()
    }
}
//?}