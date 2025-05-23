package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.annotations.ApiStatus

class BundleMeta : ItemBuildMeta() {

    var items: MutableList<ItemStack> = mutableListOf()

    fun addItem(item: ItemStack) = apply {
        items.add(item)
    }

    fun addItem(itemBuilder: com.undefined.lynx.itembuilder.ItemBuilder) = apply {
        items.add(itemBuilder.build())
    }

    fun addItems(vararg item: ItemStack) = apply {
        items.addAll(item.toList())
    }

    fun setItems(items: List<ItemStack>) = apply {
        this.items = items.toMutableList()
    }

    fun setItem(items: List<com.undefined.lynx.itembuilder.ItemBuilder>) = apply {
        setItems(items.map { it.build() })
    }

    @ApiStatus.Internal
    override fun setItemCache(itemMeta: ItemMeta) {
        val bundleMeta = itemMeta as BundleMeta
        items = bundleMeta.items
    }

    @ApiStatus.Internal
    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val bundleMeta = itemMeta as? BundleMeta ?: return itemMeta
        bundleMeta.setItems(items)
        return bundleMeta
    }

}