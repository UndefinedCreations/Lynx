package com.undefined.lynx.itembuilder.meta.book

import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.WritableBookMeta

class WriteableBookMeta : AbstractWritableBookMeta<WritableBookMeta>() {

    override fun setItemCache(itemMeta: ItemMeta) {
        super.setItemCache(itemMeta)
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        return super.setMetaFromCache(itemMeta)
    }
}