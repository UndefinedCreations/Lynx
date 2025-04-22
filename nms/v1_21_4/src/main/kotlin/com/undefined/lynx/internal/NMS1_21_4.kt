package com.undefined.lynx.internal

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.undefined.lynx.nms.NMS
import net.minecraft.world.item.component.ResolvableProfile
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object NMS1_21_4: NMS {

    override fun setSkullTexture(skullMeta: SkullMeta, texture: String): SkullMeta {
        val gameProfile = GameProfile(UUID.randomUUID(), "texture")
        gameProfile.properties.put("textures", Property("textures", texture))
        skullMeta::class.java.getDeclaredField("profile").run {
            this.isAccessible = true
            this.set(skullMeta, ResolvableProfile(gameProfile))
        }
        return skullMeta
    }

}