package com.undefined.lynx.nms

import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.Skin
import com.undefined.lynx.team.NameTagVisibility
import net.kyori.adventure.text.Component
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

        /**
         * Create a NMS objective class
         */
        fun createObjective(scoreboard: org.bukkit.scoreboard.Scoreboard, title: Component): Any

        /**
         * Changes the title in the objective
         */
        fun setTitle(objective: Any, title: Component)

        /**
         *
         * Sends the set objective packet to the list of players
         *
         * @param id
         * 0 is set the sidebar
         * 2 is update title
         * 1 is remove sidebar
         */
        fun sendClientboundSetObjectivePacket(objective: Any, id: Int, players: List<Player>)

        /**
         * Sends the set display objective packet to the list of players
         */
        fun sendClientboundSetDisplayObjectivePacket(objective: Any, players: List<Player>)

        /**
         * Sends the set score packet to the list of players
         */
        fun sendSetScorePacket(orderId: String, text: Component, objective: Any, score: Int, players: List<Player>)

        /**
         * Sends the set reset score packet to the list of players
         */
        fun sendClientboundResetScorePacket(text: String, objective: Any, players: List<Player>)

        /**
         * Create a NMS team class
         */
        fun createTeam(scoreboard: org.bukkit.scoreboard.Scoreboard, name: String): Any

        /**
         * Changes a teams prefix
         */
        fun setTeamPrefix(team: Any, prefix: Component)

        /**
         * Changes a teams suffix
         */
        fun setTeamSuffix(team: Any, suffix: Component)

        /**
         * Changes a teams ability to see friendly invisible
         */
        fun setTeamSeeFriendlyInvisibles(team: Any, canSee: Boolean)

        /**
         * Changes a teams ability to see name tags
         */
        fun setTeamNameTagVisibility(team: Any, visible: NameTagVisibility)

        /**
         * Changes a teams collision rules
         */
        fun setTeamCollisionRule(team: Any, rule: CollisionRule)

        /**
         * Changes a teams color
         */
        fun setTeamColor(team: Any, color: ChatColor)

        /**
         * Adds an entry to the team
         */
        fun addTeamEntry(team: Any, name: String)

        /**
         * Sends the set player team add or modify packet to the list of players
         */
        fun sendClientboundSetPlayerTeamPacketAddOrModify(team: Any, players: List<Player>)

        /**
         * Sends the set player team remove packet to the list of players
         */
        fun sendClientboundSetPlayerTeamPacketRemove(team: Any, players: List<Player>)

    }

}