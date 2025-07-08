package com.undefined.lynx.itembuilder

import com.undefined.lynx.NMSManager
import org.bukkit.inventory.meta.SkullMeta

@Suppress("DEPRECATION")
class SkullMeta(meta: SkullMeta) : SkullMeta by meta {

    fun setTexture(texture: String?) = apply {
        NMSManager.nms.itemBuilder.setSkullTexture(this, texture ?: "")
    }

}