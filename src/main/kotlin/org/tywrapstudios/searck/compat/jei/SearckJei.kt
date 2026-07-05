package org.tywrapstudios.searck.compat.jei

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.IFocus
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import org.tywrapstudios.searck.Searck

@JeiPlugin
object SearckJei : IModPlugin {
    lateinit var runtime: IJeiRuntime

    override fun onRuntimeAvailable(runtime: IJeiRuntime) {
        this.runtime = runtime
    }

    override fun getPluginUid(): Identifier {
        return Searck.id("jei_plugin")
    }
}