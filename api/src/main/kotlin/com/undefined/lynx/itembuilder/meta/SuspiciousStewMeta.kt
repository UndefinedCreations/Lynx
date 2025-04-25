package com.undefined.lynx.itembuilder.meta

import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SuspiciousStewMeta
import org.bukkit.potion.PotionEffect

class SuspiciousStewMeta : com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private var customEffect: HashMap<PotionEffect, Boolean> = hashMapOf()

    fun setCustomEffect(map: HashMap<PotionEffect, Boolean>) = apply {
        this.customEffect = map
    }

    fun addCustomEffect(potionEffect: PotionEffect, override: Boolean) = apply {
        customEffect[potionEffect] = override
    }

    override fun setItemCache(itemMeta: ItemMeta) {}

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val suspiciousStewMeta = itemMeta as? SuspiciousStewMeta ?: return itemMeta
        customEffect.forEach { suspiciousStewMeta.addCustomEffect(it.key, it.value) }
        return suspiciousStewMeta
    }
}