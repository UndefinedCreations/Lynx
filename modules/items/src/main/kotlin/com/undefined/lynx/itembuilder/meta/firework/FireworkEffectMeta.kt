package com.undefined.lynx.itembuilder.meta.firework

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.FireworkEffect
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta

class FireworkEffectMeta : ItemBuildMeta() {

    var effect: FireworkEffect? = null

    fun setEffect(fireworkEffect: FireworkEffect?) = apply {
        this.effect = fireworkEffect
    }


    override fun setItemCache(itemMeta: ItemMeta) {
        val fireworkMeta = itemMeta as FireworkEffectMeta
        this.effect = fireworkMeta.effect
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val fireworkMeta = itemMeta as? FireworkEffectMeta ?: return itemMeta
        fireworkMeta.effect = effect
        return fireworkMeta
    }
}