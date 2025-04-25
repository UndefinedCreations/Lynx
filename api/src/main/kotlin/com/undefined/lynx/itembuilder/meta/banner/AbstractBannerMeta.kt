package com.undefined.lynx.itembuilder.meta.banner

import org.bukkit.block.banner.Pattern
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.ItemMeta
import org.jetbrains.annotations.ApiStatus

@Suppress("UNCHECKED_CAST")
abstract class AbstractBannerMeta<T>: com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private var patterns: MutableList<Pattern> = mutableListOf()

    fun addPattern(pattern: Pattern): T = apply {
        patterns.add(pattern)
    } as T

    fun setPattern(id: Int, pattern: Pattern): T = apply {
        patterns[id] = pattern
    } as T

    fun setPatterns(patterns: List<Pattern>): T = apply {
        this.patterns = patterns.toMutableList()
    } as T

    @ApiStatus.Internal
    override fun setItemCache(itemMeta: ItemMeta) {
        val bannerMeta = itemMeta as BannerMeta
        patterns = bannerMeta.patterns
    }
    
    @ApiStatus.Internal
    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val bannerMeta = itemMeta as? BannerMeta ?: return itemMeta
        bannerMeta.patterns = patterns
        return bannerMeta
    }
}