package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.SideBar
import com.undefined.lynx.sidebar.team.Team
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ScoreboardManager : Listener {

    val activeTeams: MutableList<Team> = mutableListOf()
    val activeSideBars: MutableList<SideBar> = mutableListOf()

    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        activeTeams.filter { it.autoLoad }.forEach { it.addViewer(e.player) }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        activeTeams.filter { it.playersList.contains(e.player) }.forEach { it.playersList.remove(e.player) }
        activeSideBars.filter { it.players.contains(e.player) }.forEach { it.players.remove(e.player) }
    }

}