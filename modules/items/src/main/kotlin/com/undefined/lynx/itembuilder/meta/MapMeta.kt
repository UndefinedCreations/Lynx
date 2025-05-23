package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.Color
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView

class MapMeta : ItemBuildMeta() {

    var color: Color? = null
    var mapView: MapView? = null
    var scaling: Boolean = false

    fun setColor(color: Color?) = apply {
        this.color = color
    }

    fun setMapView(mapView: MapView?) = apply {
        this.mapView = mapView
    }

    fun setScaling(scaling: Boolean) = apply {
        this.scaling = scaling
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val mapMeta = itemMeta as MapMeta
        this.color = mapMeta.color
        this.mapView = mapMeta.mapView
        this.scaling = mapMeta.isScaling
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val mapMeta = itemMeta as? MapMeta ?: return itemMeta
        mapMeta.color = this.color
        mapMeta.mapView = this.mapView
        mapMeta.isScaling = this.scaling
        return mapMeta
    }
}