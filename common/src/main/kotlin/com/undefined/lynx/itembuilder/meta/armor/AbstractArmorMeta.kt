package com.undefined.lynx.itembuilder.meta.armor

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import org.jetbrains.annotations.ApiStatus

@Suppress("UNCHECKED_CAST")
abstract class AbstractArmorMeta<T> : ItemBuildMeta() {

    private var trimPattern: TrimPattern? = null
    private var materialPattern: TrimMaterial? = null

    fun setTrim(trim: ArmorTrim): T {
        this.trimPattern = trim.pattern
        this.materialPattern = trim.material
        return this as T
    }

    fun setTrim(
        pattern: TrimPattern,
        material: TrimMaterial
    ): T {
        this.trimPattern = pattern
        this.materialPattern = material
        return this as T
    }

    @ApiStatus.Internal
    override fun setItemCache(itemMeta: ItemMeta) {
        if (itemMeta is org.bukkit.inventory.meta.ArmorMeta) {
            val trim = itemMeta.trim
            trimPattern = trim?.pattern
            materialPattern = trim?.material
        }
    }

    @ApiStatus.Internal
    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        if (materialPattern != null && trimPattern != null) {
            val armorMeta = itemMeta as org.bukkit.inventory.meta.ArmorMeta
            armorMeta.trim = ArmorTrim(materialPattern!!, trimPattern!!)
            return armorMeta
        }
        return itemMeta
    }

}