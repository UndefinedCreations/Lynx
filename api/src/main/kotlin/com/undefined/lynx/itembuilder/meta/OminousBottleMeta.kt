package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.OminousBottleMeta

class OminousBottleMeta : com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private var amplifier: Int = 1

    fun setAmplifier(amplifier: Int) = apply {
        this.amplifier = amplifier
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val ominousBottleMeta = itemMeta as OminousBottleMeta
        this.amplifier = ominousBottleMeta.amplifier
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val ominousBottleMeta = itemMeta as? OminousBottleMeta ?: return itemMeta
        ominousBottleMeta.amplifier = amplifier
        return ominousBottleMeta
    }
}