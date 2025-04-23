package com.undefined.lynx.itembuilder.meta.firework

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.FireworkEffect
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.inventory.meta.ItemMeta

class FireworkMeta : com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private var effects: MutableList<FireworkEffect> = mutableListOf()
    private var power: Int = 1

    fun addEffect(vararg fireworkEffect: FireworkEffect) = apply {
        effects.addAll(fireworkEffect)
    }

    fun addEffects(effect: List<FireworkEffect>) = apply {
        effects.addAll(effect)
    }

    fun setEffects(effect: List<FireworkEffect>) = apply {
        effects = effect.toMutableList()
    }

    fun setPower(power: Int) = apply {
        this.power = power
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val fireworkMeta = itemMeta as FireworkMeta
        this.effects = fireworkMeta.effects
        this.power = fireworkMeta.power
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val fireworkMeta = itemMeta as? FireworkMeta ?: return itemMeta
        effects.forEach { fireworkMeta.addEffect(it) }
        fireworkMeta.power = power
        return fireworkMeta
    }
}