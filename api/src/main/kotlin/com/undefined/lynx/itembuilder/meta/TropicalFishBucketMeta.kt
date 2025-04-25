package com.undefined.lynx.itembuilder.meta

import org.bukkit.DyeColor
import org.bukkit.entity.TropicalFish.Pattern
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.TropicalFishBucketMeta

class TropicalFishBucketMeta : com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private var color: DyeColor = DyeColor.BLUE
    private var pattern: Pattern = Pattern.KOB
    private var patternColor: DyeColor = DyeColor.BLUE

    fun setBodyColor(color: DyeColor) = apply {
        this.color = color
    }

    fun setPattern(pattern: Pattern) = apply {
        this.pattern = pattern
    }

    fun setPatternColor(color: DyeColor) = apply {
        this.patternColor = color
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val tropicalFishBucketMeta = itemMeta as TropicalFishBucketMeta
        this.color = tropicalFishBucketMeta.bodyColor
        this.patternColor = tropicalFishBucketMeta.patternColor
        this.pattern = tropicalFishBucketMeta.pattern
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val tropicalFishBucketMeta = itemMeta as? TropicalFishBucketMeta ?: return itemMeta
        tropicalFishBucketMeta.bodyColor = color
        tropicalFishBucketMeta.pattern = pattern
        tropicalFishBucketMeta.patternColor = patternColor
        return tropicalFishBucketMeta
    }
}