package com.undefined.lynx.nms

import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.Skin
import com.undefined.lynx.team.NameTagVisibility
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

interface NMS {

    val nick: Nick

    val itemBuilder: ItemBuilder

    val npc: NPC

    val scoreboard: Scoreboard

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

    interface Scoreboard {

        fun createObjective(scoreboard: org.bukkit.scoreboard.Scoreboard, title: String): Any

        fun setTitle(objective: Any, title: String)

        /*
         0 is set the sidebar
         2 is update title
         1 is remove sidebar
         */
        fun sendClientboundSetObjectivePacket(objective: Any, id: Int, players: List<Player>)

        fun sendClientboundSetDisplayObjectivePacket(objective: Any, players: List<Player>)

        fun sendScorePacket(text: String, objective: Any, score: Int, players: List<Player>)

        fun sendClientboundResetScorePacket(text: String, objective: Any, players: List<Player>)

        fun createTeam(scoreboard: org.bukkit.scoreboard.Scoreboard, name: String): Any

        fun setTeamPrefix(team: Any, prefix: String)

        fun setTeamSuffix(team: Any, suffix: String)

        fun setTeamSeeFriendlyInvisibles(team: Any, canSee: Boolean)

        fun setTeamNameTagVisibility(team: Any, visible: NameTagVisibility)

        fun setTeamCollisionRule(team: Any, rule: CollisionRule)

        fun setTeamColor(team: Any, color: ChatColor)

        fun addTeamEntry(team: Any, name: String)

        fun sendClientboundSetPlayerTeamPacketAddOrModify(team: Any, players: List<Player>)

        fun sendClientboundSetPlayerTeamPacketRemove(team: Any, players: List<Player>)

    }

}