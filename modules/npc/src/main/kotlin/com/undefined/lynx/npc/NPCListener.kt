package com.undefined.lynx.npc

import com.undefined.lynx.LynxConfig
//import com.undefined.lynx.NMSManager
import com.undefined.lynx.npc.NPCManager.autoLoadNPCS
import com.undefined.lynx.npc.NPCManager.spawnedNPC
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

class NPCListener : Listener {

    @EventHandler
    fun playerWorldChangeEvent(event: PlayerChangedWorldEvent) {
        val world = event.player.world
        Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable {
            autoLoadNPCS.filter {
                it.visibleTo?.contains(event.player.uniqueId) ?: true
            }.filter {
                world == it.location.world
            }.forEach {
//                NMSManager.nms.npc.sendSpawnPacket(it.serverPlayer, it.location, listOf(event.player))
                it.resentItems(listOf(event.player))
            }
        })
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
//        spawnedNPC.filter { it.visibleTo == null }.forEach { NMSManager.nms.npc.sendSpawnPacket(it.serverPlayer, it.location, listOf(event.player)) }
    }

}