package com.undefined.lynx.itembuilder.meta.banner

import org.bukkit.inventory.meta.ItemMeta

class BannerMeta : AbstractBannerMeta<BannerMeta>() {
    override fun setItemCache(itemMeta: ItemMeta) {
        super.setItemCache(itemMeta)
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        return super.setMetaFromCache(itemMeta)
    }
}