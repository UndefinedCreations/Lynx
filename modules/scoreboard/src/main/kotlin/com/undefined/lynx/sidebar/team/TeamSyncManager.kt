package com.undefined.lynx.sidebar.team

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.nick.events.PlayerNameChangeEvent
import com.undefined.lynx.sidebar.ScoreboardManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TeamSyncManager : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
    }

    @EventHandler
    fun onNameChange(event: PlayerNameChangeEvent) {
        ScoreboardManager.trackEntryMap[event.player.uniqueId]?.let {
            for (team in it) {
                NMSManager.nms.scoreboard.removeTeamEntry(team.team, event.oldName)
                NMSManager.nms.scoreboard.addTeamEntry(team.team, event.newName)
                NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team.team, team.players())
            }
        }
    }

}