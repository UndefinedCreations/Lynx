package com.undefined.lynx.tab.layout

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.tab.TabManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object TabLayoutManager: Listener {

    internal val activeTabLayout: MutableList<AbstractTabLayout> = mutableListOf()

    internal val badTeam = TabManager.createTeam(TabManager.order(81))

    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
        Bukkit.getOnlinePlayers().forEach {
            TabManager.addTeamEntry(badTeam, it.name)
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        TabManager.addTeamEntry(badTeam, e.player.name)
        activeTabLayout.forEach {
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(badTeam, it.viewers)
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        TabManager.removeTeamEntry(badTeam, e.player.name)
        activeTabLayout.forEach {
            if (it.viewers.contains(e.player)) it.viewers.remove(e.player)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(badTeam, it.viewers)
        }
    }

}