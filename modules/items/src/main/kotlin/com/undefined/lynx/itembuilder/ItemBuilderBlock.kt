package com.undefined.lynx.itembuilder

import org.bukkit.inventory.meta.ItemMeta

interface ItemBuilderBlock<T : ItemMeta> {
    fun run(itemBuilder: T)
}