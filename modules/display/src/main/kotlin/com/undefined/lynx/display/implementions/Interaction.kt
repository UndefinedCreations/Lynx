package com.undefined.lynx.display.implementions

import com.undefined.lynx.NMSManager
import com.undefined.lynx.display.BaseDisplay
import com.undefined.lynx.util.RunBlock
import org.bukkit.Location
import org.bukkit.entity.Player

class Interaction @JvmOverloads constructor(
    location: Location,
    visibleTo: MutableList<Player>? = null,
    kotlinDSL: RunBlock<Interaction> = RunBlock{}
) : BaseDisplay(
    NMSManager.nms.display.interaction.createInteraction(location.world!!),
    visibleTo,
    location
) {

    init {
        kotlinDSL.run(this)
    }

    @JvmOverloads
    fun setWidth(width: Float, update: Boolean = false) = apply {
        NMSManager.nms.display.interaction.setWidth(display, width)
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setHeight(height: Float, update: Boolean = true) = apply {
        NMSManager.nms.display.interaction.setHeight(display, height)
        if (update) sendMetaDataUpdate()
    }

}