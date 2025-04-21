package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.block.banner.Pattern
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.annotations.ApiStatus

class BannerMeta : ItemBuildMeta() {

    private var patterns: MutableList<Pattern> = mutableListOf()

    fun addPattern(pattern: Pattern) = apply {
        patterns.add(pattern)
    }

    fun setPattern(id: Int, pattern: Pattern) = apply {
        patterns[id] = pattern
    }

    fun setPatterns(patterns: List<Pattern>) = apply {
        this.patterns = patterns.toMutableList()
    }

    @ApiStatus.Internal
    private fun setItemCache(itemMeta: ItemMeta) {
        val bannerMeta = itemMeta as BannerMeta
        patterns = bannerMeta.patterns
    }
    
    @ApiStatus.Internal
    private fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val bannerMeta = itemMeta as? BannerMeta ?: return itemMeta
        bannerMeta.patterns = patterns
        return bannerMeta
    }
}