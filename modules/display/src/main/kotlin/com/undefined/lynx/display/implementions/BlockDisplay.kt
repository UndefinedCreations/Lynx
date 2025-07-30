package com.undefined.lynx.display.implementions

import com.undefined.lynx.NMSManager
import com.undefined.lynx.util.RunBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player

class BlockDisplay @JvmOverloads constructor(
    location: Location,
    visibleTo: MutableList<Player>? = null,
    kotlinDSL: RunBlock<BlockDisplay> = RunBlock {}
) : Display(
    NMSManager.nms.display.blockDisplay.createBlockDisplay(location.world!!),
    visibleTo,
    location
) {

    init {
        kotlinDSL.run(this)
    }

    @JvmOverloads
    fun setBlock(blockData: BlockData, update: Boolean = true) = apply {
        NMSManager.nms.display.blockDisplay.setBlock(display, blockData)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setBlock(material: Material, update: Boolean = true) = setBlock(material.createBlockData(), update)

}