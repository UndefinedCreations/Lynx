package com.undefined.lynx.npc


import com.undefined.lynx.NMSManager
import com.undefined.lynx.nms.EntityInteract
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

open class NPC(
    internal val serverPlayer: Any,
    internal val visibleTo: MutableList<UUID>?,
    internal var location: Location
) {

    internal var clickActions: MutableList<EntityInteract.() -> Unit> = mutableListOf()

    private val itemStacks: HashMap<Int, ItemStack> = hashMapOf()

    fun resentItems(players: List<Player>?) {
        itemStacks.forEach { item -> NMSManager.nms.npc.setItem(serverPlayer, item.key, item.value, players?.map { it.uniqueId } ?: Bukkit.getOnlinePlayers().map { it.uniqueId }) }
    }

    fun onClick(onClick: EntityInteract.() -> Unit) {
        clickActions.add(onClick)
    }

    fun setItem(slot: Int, item: ItemStack) {
        NMSManager.nms.npc.setItem(serverPlayer, slot, item, visibleTo ?: Bukkit.getOnlinePlayers().map { it.uniqueId })
        itemStacks[slot] = item
    }

    fun setScale(scale: Double) {
        NMSManager.nms.npc.setScale(serverPlayer, scale)
        NMSManager.nms.npc.sendUpdateAttributesPacket(serverPlayer, visibleTo ?: Bukkit.getOnlinePlayers().map { it.uniqueId })
    }

    fun remove() {
        NMSManager.nms.npc.sendRemovePacket(serverPlayer, visibleTo ?: Bukkit.getOnlinePlayers().map { it.uniqueId })
        NPCManager.autoLoadNPCS.remove(this)
        NPCManager.spawnedNPC.remove(this)
    }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun addViewers(players: List<Player>) {
        NMSManager.nms.npc.sendSpawnPacket(serverPlayer, location, players)
        visibleTo?.addAll(players.map { it.uniqueId })
    }

    fun removeViewers(players: List<Player>) {
        NMSManager.nms.npc.sendRemovePacket(serverPlayer, players.map { it.uniqueId })
        visibleTo?.removeAll(players.map { it.uniqueId })
    }

    fun removeViewer(player: Player) = removeViewers(listOf(player))

    fun teleport(location: Location) {
        NMSManager.nms.npc.sendTeleportPacket(serverPlayer, location, visibleTo ?: Bukkit.getOnlinePlayers().map { it.uniqueId })
        this.location = location
    }

    fun getUUID(): UUID = NMSManager.nms.npc.getUUID(serverPlayer)

    fun getEntityID(): Int = NMSManager.nms.npc.getID(serverPlayer)

}