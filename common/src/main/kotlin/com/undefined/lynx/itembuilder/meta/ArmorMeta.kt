package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import org.jetbrains.annotations.ApiStatus

open class ArmorMeta : ItemBuildMeta() {

    private var trimPattern: TrimPattern? = null
    private var materialPattern: TrimMaterial? = null

    fun setTrim(trim: ArmorTrim): ArmorMeta {
        this.trimPattern = trim.pattern
        this.materialPattern = trim.material
        return this
    }

    fun setTrim(
        pattern: TrimPattern,
        material: TrimMaterial
    ): ArmorMeta {
        this.trimPattern = pattern
        this.materialPattern = material
        return this
    }

    @ApiStatus.Internal
    protected open fun setItemCache(itemMeta: ItemMeta) {
        if (itemMeta is org.bukkit.inventory.meta.ArmorMeta) {
            val trim = itemMeta.trim
            trimPattern = trim?.pattern
            materialPattern = trim?.material
        }
    }

    @ApiStatus.Internal
    protected open fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        if (materialPattern != null && trimPattern != null) {
            val armorMeta = itemMeta as org.bukkit.inventory.meta.ArmorMeta
            armorMeta.trim = ArmorTrim(materialPattern!!, trimPattern!!)
            return armorMeta
        }
        return itemMeta
    }

}