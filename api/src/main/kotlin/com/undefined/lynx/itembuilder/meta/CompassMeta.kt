package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.Location
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.inventory.meta.ItemMeta

class CompassMeta : com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private var lodestoneTracked = false
    private var lodestone: Location? = null

    fun setLodestoneTracked(tracked: Boolean) = apply {
        this.lodestoneTracked = tracked
    }

    fun setLodestone(lodestone: Location?) = apply {
        this.lodestone = lodestone
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val compassMeta = itemMeta as CompassMeta
        lodestoneTracked = compassMeta.isLodestoneTracked
        lodestone = compassMeta.lodestone
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val compassMeta = itemMeta as? CompassMeta ?: return itemMeta
        compassMeta.lodestone = lodestone
        compassMeta.isLodestoneTracked = lodestoneTracked
        return compassMeta
    }
}