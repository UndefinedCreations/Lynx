package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.MusicInstrument
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.MusicInstrumentMeta

class MusicInstrumentMeta : ItemBuildMeta() {

    private var instrument: MusicInstrument? = null

    fun setInstrument(instrument: MusicInstrument?) = apply {
        this.instrument = instrument
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val instrumentMeta = itemMeta as MusicInstrumentMeta
        this.instrument = instrumentMeta.instrument
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val instrumentMeta = itemMeta as? MusicInstrumentMeta ?: return itemMeta
        instrumentMeta.instrument = instrument
        return instrumentMeta
    }
}