package com.undefined.lynx.itembuilder.meta.banner

import org.bukkit.DyeColor
import org.bukkit.inventory.meta.ItemMeta

class ShieldMeta : com.undefined.lynx.itembuilder.meta.banner.AbstractBannerMeta<ShieldMeta>() {

    private var color: DyeColor? = null

    fun setBaseColor(color: DyeColor?) = apply {
        this.color = color
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        super.setItemCache(itemMeta)
        val shieldMeta = itemMeta as org.bukkit.inventory.meta.ShieldMeta
        this.color = shieldMeta.baseColor
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val shieldMeta = itemMeta as? org.bukkit.inventory.meta.ShieldMeta ?: return super.setMetaFromCache(itemMeta)
        shieldMeta.baseColor = color
        return super.setMetaFromCache(shieldMeta)
    }
}