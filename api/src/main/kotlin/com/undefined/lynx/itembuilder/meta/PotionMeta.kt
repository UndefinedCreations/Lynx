package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.Color
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType

class PotionMeta : ItemBuildMeta() {

    private var customEffect: HashMap<PotionEffect, Boolean> = hashMapOf()
    private var basePotionType: PotionType? = null
    private var color: Color? = null
    private var customName: String? = null

    fun addCustomEffect(potionEffect: PotionEffect, override: Boolean) = apply {
        customEffect[potionEffect] = override
    }

    fun setCustomEffect(map: HashMap<PotionEffect, Boolean>) = apply {
        customEffect = map
    }

    fun setBasePotionType(type: PotionType?) = apply {
        this.basePotionType = type
    }

    fun setColor(color: Color?) = apply {
        this.color = color
    }

    fun setCustomName(customName: String?) = apply {
        this.customName = customName
    }


    override fun setItemCache(itemMeta: ItemMeta) {
        val potionMeta = itemMeta as PotionMeta
        this.basePotionType = potionMeta.basePotionType
        this.color = potionMeta.color
        this.customName = potionMeta.customName
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val potionMeta = itemMeta as? PotionMeta ?: return itemMeta
        potionMeta.basePotionType = basePotionType
        potionMeta.color = color
        potionMeta.customName = customName
        customEffect.forEach { potionMeta.addCustomEffect(it.key, it.value) }
        return potionMeta
    }
}