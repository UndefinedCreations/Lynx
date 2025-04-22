package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.entity.Axolotl
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.annotations.ApiStatus

class AxolotlBucketMeta: ItemBuildMeta() {

    private var variant: Axolotl.Variant? = null

    fun setVariant(variant: Axolotl.Variant): AxolotlBucketMeta {
        this.variant = variant
        return this
    }

    @ApiStatus.Internal
    override fun setItemCache(itemMeta: ItemMeta) {
        val axolotlMeta = itemMeta as org.bukkit.inventory.meta.AxolotlBucketMeta
        this.variant = axolotlMeta.variant
    }

    @ApiStatus.Internal
    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        if (variant != null) {
            val axolotlMeta = itemMeta as org.bukkit.inventory.meta.AxolotlBucketMeta
            axolotlMeta.variant = variant!!
            return axolotlMeta
        }
        return itemMeta
    }
}