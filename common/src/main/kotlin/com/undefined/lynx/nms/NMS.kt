package com.undefined.lynx.nms

import org.bukkit.inventory.meta.SkullMeta

interface NMS {

    fun setSkullTexture(skullMeta: SkullMeta, texture: String): SkullMeta

}