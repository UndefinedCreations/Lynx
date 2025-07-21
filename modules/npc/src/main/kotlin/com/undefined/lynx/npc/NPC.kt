package com.undefined.lynx.npc


import com.undefined.lynx.NMSManager
import com.undefined.lynx.nms.EntityInteract
import com.undefined.lynx.team.NameTagVisibility
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.floor

open class NPC(
    internal val serverPlayer: Any,
    internal val serverEntity: Any,
    internal val team: Any,
    internal val visibleTo: MutableList<Player>?,
    internal var location: Location
) {

    internal var clickActions: MutableList<EntityInteract.() -> Unit> = mutableListOf()

    private val itemStacks: HashMap<Int, ItemStack> = hashMapOf()

    fun resentItems(players: List<Player> = players()) = apply {
        itemStacks.forEach { item -> NMSManager.nms.npc.setItem(serverPlayer, item.key, item.value, players) }
    }

    fun onClick(onClick: EntityInteract.() -> Unit) = apply {
        clickActions.add(onClick)
    }

    fun clearOnClick() = apply { clickActions.clear() }

    fun setItem(slot: Int, item: ItemStack, players: List<Player> = players()) = apply {
        NMSManager.nms.npc.setItem(serverPlayer, slot, item, players)
        itemStacks[slot] = item
    }

    fun setScale(scale: Double, players: List<Player> = players()) = apply {
        NMSManager.nms.npc.setScale(serverPlayer, scale)
        NMSManager.nms.npc.sendUpdateAttributesPacket(serverPlayer, players)
    }

    fun hideName(hide: Boolean, players: List<Player> = players()) = apply {
        NMSManager.nms.scoreboard.setTeamNameTagVisibility(team, if (hide) NameTagVisibility.NEVER else NameTagVisibility.ALWAYS)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
    }

    fun setPose(pose: Pose) = apply {
        NMSManager.nms.npc.setPos(serverPlayer, pose)
        NMSManager.nms.display.updateEntityData(serverPlayer, players())
    }

    fun setGravity(gravity: Boolean) = apply {
        NMSManager.nms.npc.setGravity(serverPlayer, gravity)
        NMSManager.nms.display.updateEntityData(serverPlayer, players())
    }

    fun remove() = apply {
        clearOnClick()
        visibleTo?.clear()
        removeViewers(players())
        NPCManager.autoLoadNPCS.remove(this)
        NPCManager.spawnedNPC.remove(this)
    }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun addViewers(players: List<Player>) = apply {
        NMSManager.nms.npc.sendClientboundPlayerInfoUpdatePacketAddPlayer(serverPlayer, players)
        NMSManager.nms.display.sendClientboundAddEntityPacket(serverPlayer, serverEntity, players)
        NMSManager.nms.npc.sendClientboundSetEntityDataPacket(serverPlayer, players)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
        visibleTo?.addAll(players)
    }

    fun removeViewers(players: List<Player>) = apply {
        NMSManager.nms.display.sendClientboundRemoveEntitiesPacket(serverPlayer, players)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(team, players)
        visibleTo?.removeAll(players)
    }

    fun removeViewer(player: Player) = removeViewers(listOf(player))

    fun teleport(location: Location, players: List<Player> = players()) = apply {
        NMSManager.nms.display.setEntityLocation(serverPlayer, location)
        NMSManager.nms.npc.sendTeleportPacket(serverPlayer, players)
        this.location = location
    }

    fun moveTo(location: Location, players: List<Player> = players()) = apply {
        if (location.distance(getLocation()) > 8) return teleport(location, players)

        val deltaX = toDeltaValue(getLocation().x, location.x)
        val deltaY = toDeltaValue(getLocation().y, location.y)
        val deltaZ = toDeltaValue(getLocation().z, location.z)
        val isOnGround = location.clone().subtract(0.0, 1.0, 0.0).block.type != Material.AIR

        val deltaYaw = toDeltaRotation(location.yaw)
        val deltaPitch = toDeltaRotation(location.pitch)

        NMSManager.nms.npc.sendClientboundMoveEntityPacketPosRot(
            serverPlayer,
             deltaX,
            deltaY,
            deltaZ,
            deltaYaw,
            deltaPitch,
            isOnGround,
            players()
        )
        NMSManager.nms.npc.sendClientboundRotationPacket(serverPlayer, deltaYaw, players())

        this.location = location
    }

    fun lookAt(location: Location) = apply {
        val direction = location.toVector().subtract(getLocation().toVector())
        val newLocation = getLocation().clone().apply {
            this.direction = direction
        }
        moveTo(newLocation)
    }

    private fun toDeltaRotation(rotation: Float): Byte = floor(rotation * 256.0f / 360.0f).toInt().toByte()
    private fun toDeltaValue(oldLocation: Double, newLocation: Double) = (((newLocation - oldLocation) * 32 * 128).toInt().toShort())

    fun getLocation(): Location = location

    fun getUUID(): UUID = NMSManager.nms.npc.getUUID(serverPlayer)

    fun getEntityID(): Int = NMSManager.nms.npc.getID(serverPlayer)

    private fun players(): List<Player> = (visibleTo ?: Bukkit.getOnlinePlayers().toList()).filter { it.world == getLocation().world }
}