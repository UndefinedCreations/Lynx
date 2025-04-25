package com.undefined.lynx.nms

import com.undefined.lynx.Skin
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

interface NMS {

    val nick: Nick

    val itemBuilder: ItemBuilder

    val npc: NPC

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

    interface NPC {

        fun createServerPlayer(name: String, texture: String, signature: String): Any

        fun sendSpawnPacket(serverPlayer: Any, location: Location, player: List<Player>? = null)

        fun onClick(consumer: NPCInteract.() -> Unit)

        fun setItem(serverPlayer: Any, slot: Int, itemStack: ItemStack?, players: List<UUID>?)

        fun remove(serverPlayer: Any)

        fun getUUID(serverPlayer: Any): UUID

        fun getID(serverPlayer: Any): Int

        fun sendTeleportPacket(serverPlayer: Any, location: Location, players: List<UUID>?)

    }

}