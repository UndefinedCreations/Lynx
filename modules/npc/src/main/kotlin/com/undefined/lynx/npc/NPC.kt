package com.undefined.lynx.npc

//import com.undefined.lynx.NMSManager
import com.undefined.lynx.nms.NPCInteract
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

open class NPC(
    internal val serverPlayer: Any,
    internal val visibleTo: List<UUID>?,
    internal var location: Location
) {

    internal var clickActions: MutableList<NPCInteract.() -> Unit> = mutableListOf()

    private val itemStacks: HashMap<Int, ItemStack> = hashMapOf()

    fun resentItems(players: List<Player>?) {
//        itemStacks.forEach { item -> NMSManager.nms.npc.setItem(serverPlayer, item.key, item.value, players?.map { it.uniqueId }) }
    }

    fun onClick(onClick: NPCInteract.() -> Unit) {
        clickActions.add(onClick)
    }

    fun setItem(slot: Int, item: ItemStack) {
//        NMSManager.nms.npc.setItem(serverPlayer, slot, item, visibleTo)
        itemStacks[slot] = item
    }

    fun remove() {
//        NMSManager.nms.npc.remove(serverPlayer)
        NPCManager.autoLoadNPCS.remove(this)
        NPCManager.spawnedNPC.remove(this)
    }

    fun teleport(location: Location) {
//        NMSManager.nms.npc.sendTeleportPacket(serverPlayer, location, visibleTo)
        this.location = location
    }

//    fun getUUID(): UUID = NMSManager.nms.npc.getUUID(serverPlayer)

//    fun getEntityID(): Int = NMSManager.nms.npc.getID(serverPlayer)

}