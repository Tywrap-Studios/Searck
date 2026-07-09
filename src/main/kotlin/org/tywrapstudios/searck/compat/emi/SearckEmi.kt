//? <=1.21.1 {
/*package org.tywrapstudios.searck.compat.emi

import dev.emi.emi.api.EmiApi
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.stack.EmiStack
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.compat.IngredientRole
import org.tywrapstudios.searck.compat.RecipeViewer

object SearckEmi : EmiPlugin, RecipeViewer {
    lateinit var registry: EmiRegistry

    override fun register(registry: EmiRegistry) {
        this.registry = registry
    }

    override fun containsEntries(
        role: IngredientRole,
        stack: ItemStack
    ): Boolean {
        val emiStack = EmiStack.of(stack)
        val recipes = when (role) {
            IngredientRole.OUTPUT -> EmiApi.getRecipeManager().getRecipesByOutput(emiStack)
            IngredientRole.INPUT -> EmiApi.getRecipeManager().getRecipesByInput(emiStack)
        }
        return recipes.isNotEmpty()
    }
}
*///?}