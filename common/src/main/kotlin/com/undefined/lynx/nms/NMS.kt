package com.undefined.lynx.nms

import com.undefined.lynx.Skin
import com.undefined.lynx.npc.Pose
import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.team.NameTagVisibility
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*

interface NMS {

    val nick: Nick
    val itemBuilder: ItemBuilder
    val npc: NPC
    val scoreboard: Scoreboard
    val playerMeta: PlayerMeta
    val display: Display

    interface ItemBuilder {
        fun setSkullTexture(skullMeta: SkullMeta, texture: String): SkullMeta
    }
    interface PlayerMeta {

        fun sendClientboundPlayerInfoRemovePacketList(uuid: List<UUID>, players: List<Player>)
        fun sendClientboundPlayerInfoRemovePacketListServerPlayer(players: List<Any>, viewers: List<Player>)
        fun sendClientboundPlayerInfoAddPacket(player: Any, players: List<Player>)
        fun sendClientboundPlayerInfoUpdateListedPacket(player: Any, players: List<Player>)

        fun setServerPlayerOrder(player: Any, order: Int)

        fun sendClientboundPlayerInfoRemovePacket(player: List<Player>, players: List<Player>) = sendClientboundPlayerInfoRemovePacketList(player.map { it.uniqueId }, players)
        fun sendClientboundPlayerInfoAddPacketPlayer(player: Player, players: List<Player>)
        fun sendClientboundPlayerInfoUpdateListedPacketPlayer(player: Player, players: List<Player>)

        fun sendClientboundPlayerInfoUpdateListedOrderPacket(player: Any, players: List<Player>)

        fun sendClientboundPlayerInfoUpdateLatencyPacket(players: List<Any>, viewers: List<Player>)

        fun setName(player: Any, name: String)
        fun setSkin(player: Any, texture: String, signature: String)

        fun setLatency(player: Any, latency: Int)


    }
    interface Nick {
        fun setSkin(player: Player, texture: String, signature: String)
        fun setName(player: Player, name: String)
        fun getSkin(player: Player): Skin
        fun sendClientboundRespawnPacket(player: Player)
        fun sendClientboundGameEventPacket(player: Player)
        fun updateAbilities(player: Player)
    }
    interface NPC {
        fun createServerPlayer(name: String, texture: String, signature: String): Any
        fun sendClientboundPlayerInfoUpdatePacketAddPlayer(serverPlayer: Any, players: List<Player>)
        fun sendClientboundSetEntityDataPacket(serverPlayer: Any, players: List<Player>)
        fun getName(serverPlayer: Any): String
        fun onClick(consumer: EntityInteract.() -> Unit)
        fun setItem(serverPlayer: Any, slot: Int, itemStack: ItemStack?, players: List<Player>)
        fun getUUID(serverPlayer: Any): UUID
        fun getID(serverPlayer: Any): Int
        fun sendTeleportPacket(serverPlayer: Any, players: List<Player>)
        fun setScale(serverPlayer: Any, scale: Double)
        fun sendUpdateAttributesPacket(serverPlayer: Any, players: List<Player>)
        fun setPos(serverPlayer: Any, pose: Pose)
        fun setGravity(serverPlayer: Any, gravity: Boolean)
        fun sendClientboundMoveEntityPacketPosRot(
            serverPlayer: Any,
            deltaX: Short,
            deltaY: Short,
            deltaZ: Short,
            deltaYaw: Byte,
            deltaPitch: Byte,
            onGround: Boolean,
            players: List<Player>
        )
        fun sendClientboundRotationPacket(
            serverPlayer: Any,
            deltaYaw: Byte,
            players: List<Player>
        )
    }
    interface Scoreboard {

        /**
         * Create a NMS objective class
         */
        fun createObjective(scoreboard: org.bukkit.scoreboard.Scoreboard, title: String): Any

        /**
         * Changes the title in the objective
         */
        fun setTitle(objective: Any, title: String)

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
        fun sendSetScorePacket(orderId: String, text: String, objective: Any, score: Int, players: List<Player>)

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
        fun setTeamPrefix(team: Any, prefix: String)


        /**
         * Changes a teams suffix
         */
        fun setTeamSuffix(team: Any, suffix: String)

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

        fun removeTeamEntry(team: Any, name: String)

        fun getTeamEntry(team: Any): List<String>

        /**
         * Sends the set player team add or modify packet to the list of players
         */
        fun sendClientboundSetPlayerTeamPacketAddOrModify(team: Any, players: List<Player>)

        /**
         * Sends the set player team remove packet to the list of players
         */
        fun sendClientboundSetPlayerTeamPacketRemove(team: Any, players: List<Player>)

    }
    interface Display {
        fun setEntityLocation(display: Any, location: Location)
        fun createServerEntity(display: Any, world: World): Any
        fun sendClientboundAddEntityPacket(display: Any, serverEntity: Any, players: List<Player>)
        fun setScale(display: Any, vector3f: Vector3f)
        fun setLeftRotation(display: Any, quaternionf: Quaternionf)
        fun setRightRotation(display: Any, quaternionf: Quaternionf)
        fun setTranslation(display: Any, vector3f: Vector3f)
        fun setInterpolationDuration(display: Any, duration: Int)
        fun setInterpolationDelay(display: Any, duration: Int)
        fun setTeleportDuration(display: Any, duration: Int)
        fun setBillboardRender(display: Any, byte: Byte)
        fun setBrightnessOverride(display: Any, int: Int)
        fun setViewRange(display: Any, view: Float)
        fun setShadowRadius(display: Any, shadowRadius: Float)
        fun setShadowStrength(display: Any, shadowStrength: Float)
        fun setWidth(display: Any, width: Float)
        fun setHeight(display: Any, height: Float)
        fun updateEntityData(display: Any, players: List<Player>)
        fun sendClientboundRemoveEntitiesPacket(display: Any, players: List<Player>)

        val textDisplay: TextDisplay
        val blockDisplay: BlockDisplay
        val itemDisplay: ItemDisplay
        val interaction: Interaction

        interface TextDisplay {

            fun createTextDisplay(world: World): Any
            fun setText(display: Any, json: String)
            fun setLineWidth(display: Any, width: Int)
            fun setBackgroundColor(display: Any, backgroundID: Int)
            fun setTextOpacity(display: Any, textOpacity: Byte)
            fun setStyleFlags(display: Any, styleFlags: Byte)
            fun getStyleFlag(display: Any): Byte

        }

        interface BlockDisplay {
            fun createBlockDisplay(world: World): Any
            fun setBlock(display: Any, block: BlockData)
        }

        interface ItemDisplay {
            fun createItemDisplay(world: World): Any
            fun setItem(display: Any, itemStack: ItemStack)
        }

        interface Interaction {
            fun createInteraction(world: World): Any
            fun setWidth(display: Any, width: Float)
            fun setHeight(display: Any, height: Float)
        }

    }
}