package com.undefined.lynx.display.implementions

import com.undefined.lynx.NMSManager
import com.undefined.lynx.util.RunBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemDisplay @JvmOverloads constructor(
    location: Location,
    visibleTo: MutableList<Player>? = null,
    autoLoad: Boolean = true,
    kotlinDSL: RunBlock<ItemDisplay> = RunBlock{}
) : Display(
    NMSManager.nms.display.itemDisplay.createItemDisplay(location.world!!),
    visibleTo,
    location,
    autoLoad
) {

    init {
        kotlinDSL.run(this)
    }
    @JvmOverloads
    fun setItem(itemStack: ItemStack, update: Boolean = true) = apply {
        NMSManager.nms.display.itemDisplay.setItem(display, itemStack)
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setItem(material: Material, update: Boolean = true) = setItem(ItemStack(material), update)

}