package com.undefined.lynx.display

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.display.implementions.Display
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.UUID

object DisplayManager : Listener {

    internal val activeDisplay: MutableList<BaseDisplay> = mutableListOf()

    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)

        NMSManager.nms.npc.onClick {
            activeDisplay.firstOrNull { it.getEntityID() == this.entityID }?.let {
                Bukkit.getScheduler().runTask(LynxConfig.javaPlugin, Runnable {
                    for (run in it.clicks) run.run(this)
                })
            }
        }

    }

    @EventHandler
    fun worldChangeEvent(event: PlayerChangedWorldEvent) {
        val world = event.player.world
        Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable {
            for (display in activeDisplay.filter { it.visibleTo?.contains(event.player) ?: true }.filter { world == it.location.world }.toList()) {
                display.addViewer(event.player)
            }
        })
    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        for (display in activeDisplay.filter { it.visibleTo == null }) {
            display.addViewer(event.player)
        }
    }

    @JvmStatic
    fun <T: Display> getDisplayEntity(uuid: UUID): T? = activeDisplay.firstOrNull { it.getUuid() == uuid } as? T
    @JvmStatic
    fun getAllDisplayEntities(): List<BaseDisplay> = activeDisplay
}

fun <T: Display> World.getDisplayEntity(uuid: UUID): T? = DisplayManager.getDisplayEntity(uuid)

fun World.getAllDisplayEntities(): List<BaseDisplay> = DisplayManager.getAllDisplayEntities()