package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.sidebar.team.Team
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ScoreboardManager : Listener {

    @JvmStatic
    val activeTeams: MutableList<Team> = mutableListOf()
    @JvmStatic
    val activeSidebars: MutableList<Sidebar> = mutableListOf()

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
        activeSidebars.filter { it.players.contains(e.player) }.forEach { it.players.remove(e.player) }
    }

}