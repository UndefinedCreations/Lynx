package com.undefined.lynx.nms

import com.undefined.lynx.Skin
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

interface NMS {

    val nick: Nick

    val itemBuilder: ItemBuilder

    interface ItemBuilder {

        fun setSkullTexture(skullMeta: SkullMeta, texture: String): SkullMeta

    }

    interface Nick {

        fun setSkin(player: Player, texture: String, signature: String)

        fun setName(player: Player, name: String)

        fun getSkin(player: Player): Skin

        fun sendClientboundPlayerInfoRemovePacket(player: Player)

        fun sendClientboundPlayerInfoAddPacket(player: Player)

        fun sendClientboundPlayerInfoUpdateListedPacket(player: Player)

        fun sendClientboundRespawnPacket(player: Player)

        fun sendClientboundGameEventPacket(player: Player)

        fun updateAbilities(player: Player)

    }

}