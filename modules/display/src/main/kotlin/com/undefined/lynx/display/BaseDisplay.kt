package com.undefined.lynx.display

import com.undefined.lynx.NMSManager
import com.undefined.lynx.nms.EntityInteract
import com.undefined.lynx.util.RunBlock
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

abstract class BaseDisplay(
    val display: Any,
    internal val visibleTo: MutableList<Player>?,
    internal var location: Location
) {

    internal val serverEntity: Any?

    internal var clicks: MutableList<RunBlock<EntityInteract>> = mutableListOf()

    init {
        NMSManager.nms.entity.setEntityLocation(display, location)
        serverEntity = NMSManager.nms.entity.createServerEntity(display, location.world!!)
        NMSManager.nms.entity.sendClientboundAddEntityPacket(display, serverEntity, players().toList())
        DisplayManager.activeDisplay.add(this)
    }

    fun onClick(run: RunBlock<EntityInteract>) = apply {
        clicks.add(run)
    }

    fun clearOnClick() = apply {
        clicks.clear()
    }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun addViewers(players: List<Player>) = apply {
        NMSManager.nms.entity.sendClientboundAddEntityPacket(display, serverEntity, players)
        NMSManager.nms.entity.updateEntityData(display, players)
        visibleTo?.addAll(players)
    }

    fun removeViewer(player: Player) = removeViewers(listOf(player))

    fun removeViewers(players: List<Player>) = apply {
        NMSManager.nms.entity.sendClientboundRemoveEntitiesPacket(display, players)
        visibleTo?.removeAll(players)
    }

    fun getUuid(): UUID = NMSManager.nms.npc.getUUID(display)

    fun getEntityID(): Int = NMSManager.nms.npc.getID(display)

    fun remove() = apply {
        removeViewers(players().toList())
        visibleTo?.clear()
        clicks.clear()
        DisplayManager.activeDisplay.remove(this)
    }

    fun teleport(location: Location) = apply {
        NMSManager.nms.entity.setEntityLocation(display, location)
        NMSManager.nms.npc.sendTeleportPacket(display, players().toList())
        this.location = location
    }

    @JvmOverloads
    fun sendMetaDataUpdate(players: List<Player> = players().toList()) = apply {
        NMSManager.nms.entity.updateEntityData(display, players)
    }

    private fun players() = visibleTo ?: Bukkit.getOnlinePlayers()

}