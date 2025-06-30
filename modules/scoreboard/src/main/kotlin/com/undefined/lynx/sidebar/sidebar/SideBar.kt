package com.undefined.lynx.sidebar.sidebar

import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.sidebar.lines.EmptyLine
import com.undefined.lynx.sidebar.sidebar.lines.Line
import com.undefined.lynx.sidebar.sidebar.lines.PlayerLine
import com.undefined.lynx.sidebar.sidebar.lines.SideBarTeam
import com.undefined.lynx.sidebar.sidebar.lines.TeamLine
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard
import kotlin.io.path.Path

class SideBar(
    title: String,
    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    kotlinDSL: SideBar.() -> Unit = {}
) {

    private val players: MutableList<Player> = mutableListOf()
    private val objective = NMSManager.nms.scoreboard.createObjective(scoreboard, title)
    private val lines: MutableList<Line> = mutableListOf()

    init {
        kotlinDSL()
    }

    fun updateDynamicLines() =
        apply { lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.sideBarTeam.team, players) } }

    fun setTitle(title: String) = apply {
        NMSManager.nms.scoreboard.setTitle(objective, title)
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 2, players)
    }

    fun addEmptyLine() = apply {
        if (lines.size >= 15) return@apply
        val orderID = nextOrderId()
        NMSManager.nms.scoreboard.sendScorePacket("$orderID ", objective, 0, players)
        lines.add(EmptyLine(" ", orderID))
    }

    fun addBlankLine() = addEmptyLine()

    fun addLine(line: String) = apply {
        if (lines.size >= 15) return@apply
        val orderID = nextOrderId()
        NMSManager.nms.scoreboard.sendScorePacket("$orderID$line", objective, 0, players)
        lines.add(Line(line, orderID))
    }

    fun removeLine(line: String) = apply {
        lines.firstOrNull { it.text == line }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket("${this.order}${this.text}", objective, players)
            lines.remove(this)
        }
    }

    fun removeDynamicLine(id: Any) = apply {
        lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket(this.order, objective, players)
            lines.remove(this)
        }
    }

    fun addDynamicPlayerLine(id: Any, run: Player.() -> String) = apply {
        val pair = dynamicLineCheck(id) ?: return@apply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)

        players.forEach {
            NMSManager.nms.scoreboard.setTeamPrefix(pair.first, run(it))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, listOf(it))
        }

        NMSManager.nms.scoreboard.sendScorePacket(pair.second, objective, 0, players)
        val line = PlayerLine(SideBarTeam(pair.first, id), pair.second, run)
        lines.add(line)
    }

    fun modifyDynamicPlayerLine(id: Any, run: Player.() -> String) = apply {
        val playerLine = lines.filterIsInstance<PlayerLine>().firstOrNull() { it.sideBarTeam.id == id } ?: return@apply
        players.forEach {
            NMSManager.nms.scoreboard.setTeamPrefix(playerLine, run(it))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(playerLine, listOf(it))
        }
        playerLine.run = run
    }

    fun addDynamicLine(id: Any, line: String) = apply {
        val pair = dynamicLineCheck(id) ?: return@apply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, players)
        NMSManager.nms.scoreboard.sendScorePacket(pair.second, objective, 0, players)
        val line = TeamLine(line, SideBarTeam(pair.first, id), pair.second)
        lines.add(line)
    }

    fun modifyDynamicLine(id: Any, line: String, update: Boolean = true) = apply {
        val teamLine = lines.filterIsInstance<TeamLine>().firstOrNull() { it.sideBarTeam.id == id } ?: return@apply
        NMSManager.nms.scoreboard.setTeamPrefix(teamLine.sideBarTeam.team, line)
        teamLine.text = line
        if (update) NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(teamLine, players)
    }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun addViewers(list: List<Player>) = apply {
        val list = list.filter { !players.contains(it) }
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 0, list)
        NMSManager.nms.scoreboard.sendClientboundSetDisplayObjectivePacket(objective, list)

        lines.forEach {
            when (it) {
                is PlayerLine -> {
                    list.forEach { player ->
                        NMSManager.nms.scoreboard.setTeamPrefix(it.sideBarTeam.team, it.run(player))
                        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.sideBarTeam.team, listOf(player))
                    }
                }
                is TeamLine -> NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.sideBarTeam.team, players)
                else -> NMSManager.nms.scoreboard.sendScorePacket(it.text, objective, 0, list)
            }
        }
        players.addAll(list)
    }

    fun removeViewer(player: Player) = removeViewers(listOf(player))

    fun removeViewers(list: List<Player>) = apply {
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 1, list)
        lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(it.sideBarTeam.team, list) }
        players.removeAll(list)
    }

    private fun dynamicLineCheck(id: Any): Pair<Any, String>? {
        if (lines.size >= 15) return null
        val teams = lines.filterIsInstance<TeamLine>()
        teams.firstOrNull { it.sideBarTeam.id == id }?.let { return null }
        val newTeam = NMSManager.nms.scoreboard.createTeam(scoreboard, id.toString())
        val order = nextOrderId()
        return Pair(newTeam, order)
    }

    private fun orderToInt(string: String): Int {
        return string.split("§")[1].toCharArray()[0].lowercaseChar() - 'a'
    }
    private fun order(time: Int): String {
        return "§" + ('a'.code + time).toChar().toString() + ChatColor.RESET
    }

    private fun nextOrderId(): String {
        return order((lines.maxOfOrNull { orderToInt(it.order) } ?: 0) + 1)
    }
}

fun sidebar(
    title: String,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    kotlinDSL: SideBar.() -> Unit = {}
): SideBar = SideBar(title, scoreboard, kotlinDSL)