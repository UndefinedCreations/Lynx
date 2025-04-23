package com.undefined.lynx.itembuilder.meta.armor

import org.bukkit.Color
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta

class LeatherArmorMeta : com.undefined.lynx.itembuilder.meta.armor.AbstractArmorMeta<LeatherArmorMeta>() {

    private var color: Color? = null

    fun setColor(color: Color?) = apply {
        this.color = color
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        super.setItemCache(itemMeta)
        val leatherMeta = itemMeta as LeatherArmorMeta
        color = leatherMeta.color
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val leatherMeta = itemMeta as? LeatherArmorMeta ?: return super.setMetaFromCache(itemMeta)
        leatherMeta.setColor(color)
        return leatherMeta
    }
}