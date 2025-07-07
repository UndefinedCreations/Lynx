package com.undefined.lynx.sidebar.team

import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.order
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import java.util.*

abstract class AbstractTeam(
    internal open val autoLoad: Boolean = true,
    scoreboard: Scoreboard,
    private val async: Boolean = false,
    order: Int = 1,
) {

    internal val playersList: MutableList<Player> = mutableListOf()
    internal val team = NMSManager.nms.scoreboard.createTeam(scoreboard, "${order(order)}${UUID.randomUUID()}")

    internal fun setPrefixJson(prefix: String) {
        NMSManager.nms.scoreboard.setTeamPrefix(team, prefix)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
    }

    internal fun setSuffixJson(suffix: String) {
        NMSManager.nms.scoreboard.setTeamSuffix(team, suffix)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players())
    }

    internal fun players(): List<Player> = if (autoLoad) Bukkit.getOnlinePlayers().toList() else playersList

}