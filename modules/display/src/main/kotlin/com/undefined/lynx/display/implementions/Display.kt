package com.undefined.lynx.display.implementions

import com.undefined.lynx.NMSManager
import com.undefined.lynx.display.BaseDisplay
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.joml.Quaternionf
import org.joml.Vector3f

abstract class Display(
    display: Any,
    visibleTo: MutableList<Player>?,
    location: Location
) : BaseDisplay(
    display,
    visibleTo,
    location
) {

    init {
        setInterpolationDelay(0, false)
        setInterpolationDuration(0, false)
        sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setScale(
        x: Double = 1.0,
        y: Double = 1.0,
        z: Double = 1.0,
        update: Boolean = true
    ) = setScale(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()), update)

    @JvmOverloads
    fun setScale(
        vector3f: Vector3f,
        update: Boolean = true
    ) = apply {
        NMSManager.nms.display.setScale(display, vector3f)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setLeftRotation(
        w: Double = 0.0,
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        update: Boolean = true
    ) = setLeftRotation(Quaternionf(x, y, z, w), update)

    @JvmOverloads
    fun setLeftRotation(leftRotation: Quaternionf, update: Boolean = true) = apply {
        NMSManager.nms.display.setLeftRotation(display, leftRotation)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setTranslation(
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        update: Boolean = true
    ) = setTranslation(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()), update)

    @JvmOverloads
    fun setTranslation(vector3f: Vector3f, update: Boolean = true) = apply {
        NMSManager.nms.display.setTranslation(display, vector3f)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setRightRotation(
        w: Double = 0.0,
        x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 0.0,
        update: Boolean = true
    ) = setRightRotation(Quaternionf(x, y, z, w), update)

    @JvmOverloads
    fun setRightRotation(rightRotation: Quaternionf, update: Boolean = true) = apply {
        NMSManager.nms.display.setLeftRotation(display, rightRotation)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setInterpolationDuration(duration: Int, update: Boolean = true) = apply {
        NMSManager.nms.display.setInterpolationDuration(display, duration)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setTeleportDuration(duration: Int, update: Boolean = true) = apply {
        NMSManager.nms.display.setTeleportDuration(display, duration)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setInterpolationDelay(duration: Int, update: Boolean = true) = apply {
        NMSManager.nms.display.setInterpolationDelay(display, duration)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setBillboard(billboard: Display.Billboard, update: Boolean = true) = apply {
        NMSManager.nms.display.setBillboardRender(
            display,
            when(billboard) {
                Display.Billboard.FIXED -> 0
                Display.Billboard.VERTICAL -> 1
                Display.Billboard.HORIZONTAL -> 2
                Display.Billboard.CENTER -> 3
            }
        )
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setBrightnessOverride(brightness: Display.Brightness, update: Boolean = true) = apply {
        val int = brightness.blockLight shl 4 or (brightness.skyLight shl 20)
        NMSManager.nms.display.setBrightnessOverride(display, int)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setViewRange(view: Float, update: Boolean = true) = apply {
        NMSManager.nms.display.setViewRange(display, view)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setShadowRadius(shadowRadius: Float, update: Boolean = true) = apply {
        NMSManager.nms.display.setShadowRadius(display, shadowRadius)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setShadowStrength(shadowStrength: Float, update: Boolean = true) = apply {
        NMSManager.nms.display.setShadowStrength(display, shadowStrength)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setWidth(width: Float, update: Boolean = true) = apply {
        NMSManager.nms.display.setWidth(display, width)
        if (update) sendMetaDataUpdate()
    }

    @JvmOverloads
    fun setHeight(height: Float, update: Boolean = true) = apply {
        NMSManager.nms.display.setHeight(display, height)
        if (update) sendMetaDataUpdate()
    }

}