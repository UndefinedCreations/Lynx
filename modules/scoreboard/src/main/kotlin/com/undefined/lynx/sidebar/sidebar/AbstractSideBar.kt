package com.undefined.lynx.sidebar.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.order
import com.undefined.lynx.sidebar.sidebar.line.Line
import com.undefined.lynx.sidebar.sidebar.line.PlayerLine
import com.undefined.lynx.sidebar.sidebar.line.PlayerTimerLine
import com.undefined.lynx.sidebar.sidebar.line.SidebarTeam
import com.undefined.lynx.sidebar.sidebar.line.StaticTimerLine
import com.undefined.lynx.sidebar.sidebar.line.TeamLine
import com.undefined.lynx.sidebar.sidebar.line.TimerLine
import com.undefined.lynx.sidebar.toJson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

abstract class AbstractSideBar(
    title: String,
    internal val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
) {

    internal val players: MutableList<Player> = mutableListOf()
    internal val objective = NMSManager.nms.scoreboard.createObjective(scoreboard, title.toJson())
    internal val lines: MutableList<Line> = mutableListOf()

    var updateTimerTick = 20

    internal fun setTitleJson(title: String) {
        NMSManager.nms.scoreboard.setTitle(objective, title)
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 2, players)
    }

    internal fun addLineWithJson(json: String) {
        if (lines.size >= 15) return
        val orderId = nextOrderId()
        NMSManager.nms.scoreboard.sendSetScorePacket(orderId, json, objective, 0, players)
        lines.add(Line(json, orderId))
    }

    internal fun removeLineJson(json: String) {
        lines.firstOrNull { it.text == json }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket(this.order, objective, players)
            lines.remove(this)
        }
    }

    internal fun addDynamicPlayerLineJson(id: Any, run: Player.() -> String) {
        val pair = dynamicLineCheck(id) ?: return
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        for (player in players) updateTeamPrefix(pair.first, run(player), listOf(player))
        NMSManager.nms.scoreboard.sendSetScorePacket(pair.second, "".toJson(), objective, 0, players)
        lines.add(PlayerLine(SidebarTeam(pair.first, id), pair.second, run))
    }

    internal fun modifyDynamicPlayerLineJson(id: Any, run: Player.() -> String) {
        val playerLine = lines.filterIsInstance<PlayerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return
        for (player in players) updateTeamPrefix(playerLine.sideBarTeam.team, run(player), listOf(player))
        playerLine.run = run
    }

    internal fun addDynamicLineJson(id: Any, line: String) {
        val pair = dynamicLineCheck(id) ?: return
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        updateTeamPrefix(pair.first, line, players)
        NMSManager.nms.scoreboard.sendSetScorePacket(pair.second, "".toJson(), objective, 0, players)
        lines.add(TeamLine("".toJson(), SidebarTeam(pair.first, id), pair.second))
    }

    internal fun modifyDynamicLineJson(id: Any, line: String, update: Boolean = true) {
        val teamLine = lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id } ?: return
        NMSManager.nms.scoreboard.setTeamPrefix(teamLine.sideBarTeam.team, line)
        teamLine.text = line
        if (update) NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(teamLine.sideBarTeam.team, players)
    }

    internal fun addDynamicTimerLineJson(id: Any, ticks: Int = updateTimerTick, async: Boolean = true, run: () -> String) {
        val pair = dynamicLineCheck(id) ?: return
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        val line = StaticTimerLine(SidebarTeam(pair.first, id), pair.second, run, null)
        runDynamicTimer({ updateTeamPrefix(pair.first, line.run(), players) }.apply { this() }, pair.second, ticks, line, async)
        lines.add(line)
    }

    internal fun modifyDynamicTimerLineJson(id: Any, run: () -> String) {
        val line = lines.filterIsInstance<StaticTimerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return
        line.run = run
        updateTeamPrefix(line.sideBarTeam.team, run(), players)
    }

    internal fun addDynamicPlayerTimerLineJson(id: Any, ticks: Int = updateTimerTick, async: Boolean = false, run: Player.() -> String) {
        val pair = dynamicLineCheck(id) ?: return
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        val line = PlayerTimerLine(SidebarTeam(pair.first, id), pair.second, run, null)
        runDynamicTimer({ for (player in players) updateTeamPrefix(pair.first, line.run(player), listOf(player)) }.apply { this() }, pair.second, ticks, line, async)
        lines.add(line)
    }

    internal fun modifyDynamicPlayerTimerLineJson(id: Any, run: Player.() -> String) {
        val line = lines.filterIsInstance<PlayerTimerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return
        line.run = run
        for (player in players) updateTeamPrefix(line.sideBarTeam.team, run(player), listOf(player))
    }

    internal fun updateTeamPrefix(team: Any, prefix: String, players: List<Player>) {
        NMSManager.nms.scoreboard.setTeamPrefix(team, prefix)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
    }

    private fun runDynamicTimer(runnable: Runnable, order: String, ticks: Int, line: TimerLine, async: Boolean) {
        NMSManager.nms.scoreboard.sendSetScorePacket(order, "".toJson(), objective, 0, players)
        line.task = if (async) Bukkit.getScheduler().runTaskTimerAsynchronously(LynxConfig.javaPlugin, runnable, ticks.toLong(), ticks.toLong())
        else Bukkit.getScheduler().runTaskTimer(LynxConfig.javaPlugin, runnable, ticks.toLong(), ticks.toLong())
    }

    private fun dynamicLineCheck(id: Any): Pair<Any, String>? {
        if (lines.size >= 15) return null
        val teams = lines.filterIsInstance<TeamLine>()
        teams.firstOrNull { it.sideBarTeam.id == id }?.let { return null }
        val newTeam = NMSManager.nms.scoreboard.createTeam(scoreboard, id.toString())
        val order = nextOrderId()
        return Pair(newTeam, order)
    }

    private fun orderToIndex(order: Char): Int = order.code
    private fun nextOrderId(): String = order((lines.maxOfOrNull { it.order.toCharArray().first().code } ?: 0) + 1)

}