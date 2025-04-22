package com.undefined.lynx.itembuilder

import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.annotations.ApiStatus

abstract class ItemBuildMeta {

    @ApiStatus.Internal
    protected abstract fun setItemCache(itemMeta: ItemMeta)

    @ApiStatus.Internal
    protected abstract fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta

}