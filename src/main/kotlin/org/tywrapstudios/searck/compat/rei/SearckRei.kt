package org.tywrapstudios.searck.compat.rei

import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.view.ViewSearchBuilder
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.compat.IngredientRole
import org.tywrapstudios.searck.compat.RecipeViewer

object SearckRei : REIClientPlugin, RecipeViewer {
    override fun containsEntries(role: IngredientRole, stack: ItemStack): Boolean {
        val builder = when (role) {
            IngredientRole.OUTPUT -> ViewSearchBuilder.builder()
                .addRecipesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack))
            IngredientRole.INPUT -> ViewSearchBuilder.builder()
                .addUsagesFor(EntryStack.of(VanillaEntryTypes.ITEM, stack))
        }

        return builder.buildMapInternal().isNotEmpty()
    }
}