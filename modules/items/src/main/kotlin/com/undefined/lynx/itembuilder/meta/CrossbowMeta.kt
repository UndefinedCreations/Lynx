package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import org.bukkit.inventory.meta.ItemMeta

class CrossbowMeta : ItemBuildMeta() {

    var projectiles: MutableList<ItemStack> = mutableListOf()

    fun setChargedProjectiles(items: List<ItemStack>) = apply {
        projectiles = items.toMutableList()
    }

    fun addChargedProjectile(vararg item: ItemStack) = apply {
        projectiles.addAll(item)
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val crossbowMeta = itemMeta as CrossbowMeta
        projectiles = crossbowMeta.chargedProjectiles
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val crossbowMeta = itemMeta as? CrossbowMeta ?: return itemMeta
        crossbowMeta.setChargedProjectiles(projectiles)
        return crossbowMeta
    }
}