package com.undefined.lynx.itembuilder

import com.undefined.lynx.NMSManager
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerProfile

@Suppress("DEPRECATION")
class SkullMeta(meta: SkullMeta) : SkullMeta by meta {

    fun setTexture(texture: String?) = apply {
        NMSManager.nms.itemBuilder.setSkullTexture(this, texture ?: "")
    }

}