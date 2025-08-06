package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.sidebar.team.Team
import com.undefined.lynx.sidebar.team.TeamSyncManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object ScoreboardManager : Listener {

    @JvmStatic
    val activeTeams: MutableList<Team> = mutableListOf()
    @JvmStatic
    val activeSidebars: MutableList<Sidebar> = mutableListOf()

    internal val trackEntryMap: HashMap<UUID, MutableSet<Team>> = hashMapOf()

    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
        setUpTeamSyncManager()
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

    private fun setUpTeamSyncManager() {
        try {
            Class.forName("com.undefined.lynx.nick.events.PlayerNameChangeEvent")
            TeamSyncManager()
        } catch (e: Exception) {
            println("Event wasn't found")
        }
    }

}