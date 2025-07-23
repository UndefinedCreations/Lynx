package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.Sidebar
import org.bukkit.entity.Player

open class BasicLine {

    private val EMPTY_JSON = "".toJson()

    internal lateinit var sideBar: Sidebar
    internal lateinit var team: Any
    internal lateinit var orderId: String

    internal open fun setUpLine(sidebar: Sidebar) {
        sideBar = sidebar
        orderId = sidebar.nextOrderId()
        team = NMSManager.nms.scoreboard.createTeam(sidebar.scoreboard, this.toString())
        NMSManager.nms.scoreboard.addTeamEntry(team, orderId)
        NMSManager.nms.scoreboard.sendSetScorePacket(orderId, EMPTY_JSON, sideBar.objective, 0, sideBar.players)
    }

    internal open fun addPlayers(players: List<Player>) {
        NMSManager.nms.scoreboard.sendSetScorePacket(orderId, EMPTY_JSON, sideBar.objective, 0, players)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
    }

    internal fun removePlayers(players: List<Player>) {
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(team, players)
    }
}