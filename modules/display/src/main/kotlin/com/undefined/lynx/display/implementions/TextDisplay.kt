package com.undefined.lynx.display.implementions

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.util.RunBlock
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player

class TextDisplay @JvmOverloads constructor(
    location: Location,
    visibleTo: MutableList<Player>? = null,
    kotlinDSL: RunBlock<TextDisplay> = RunBlock{}
) : Display(
    display = NMSManager.nms.display.textDisplay.createTextDisplay(location.world!!),
    visibleTo = visibleTo,
    location = location
) {

    init {
        kotlinDSL.run(this)
    }
    @JvmOverloads
    fun setText(string: String, update: Boolean = true) = apply {
        NMSManager.nms.display.textDisplay.setText(display, string.toJson())
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setText(component: Component, update: Boolean = true) = apply {
        NMSManager.nms.display.textDisplay.setText(display, component.toJson())
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setLineWidth(width: Int, update: Boolean = true) = apply {
        NMSManager.nms.display.textDisplay.setLineWidth(display, width)
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setBackgroundColor(background: Int, update: Boolean = true) = apply {
        NMSManager.nms.display.textDisplay.setBackgroundColor(display, background)
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setBackgroundColor(background: Color, update: Boolean = true) = setBackgroundColor(background.asARGB(), update)
    @JvmOverloads
    fun setTextOpacity(textOpacity: Byte, update: Boolean = true) = apply {
        NMSManager.nms.display.textDisplay.setTextOpacity(display, textOpacity)
        if (update) sendMetaDataUpdate()
    }
    @JvmOverloads
    fun setAlignment(alignment: org.bukkit.entity.TextDisplay.TextAlignment, update: Boolean = true) = apply {
        when (alignment) {
            org.bukkit.entity.TextDisplay.TextAlignment.LEFT -> {
                setFlag(8, true)
                setFlag(16, false)
            }
            org.bukkit.entity.TextDisplay.TextAlignment.RIGHT -> {
                setFlag(8, false)
                setFlag(16, true)
            }
            org.bukkit.entity.TextDisplay.TextAlignment.CENTER -> {
                setFlag(8, false)
                setFlag(16, false)
            }
        }
        if (update) sendMetaDataUpdate()
    }

    private fun setFlag(flag: Int, set: Boolean) {
        var flagBits: Byte = NMSManager.nms.display.textDisplay.getStyleFlag(display)
        flagBits = if (set) {
            (flagBits.toInt() or flag).toByte()
        } else {
            (flagBits.toInt() and flag.inv()).toByte()
        }
        NMSManager.nms.display.textDisplay.setStyleFlags(display, flagBits)
    }
}