package com.undefined.lynx.itembuilder.meta.armor

import org.bukkit.inventory.meta.ItemMeta

class ArmorMeta : AbstractArmorMeta<ArmorMeta>() {

    override fun setItemCache(itemMeta: ItemMeta) {
        super.setItemCache(itemMeta)
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        return super.setMetaFromCache(itemMeta)
    }
}