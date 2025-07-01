package com.undefined.lynx.sidebar.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.ScoreboardManager
import com.undefined.lynx.sidebar.checkAsyncAndApply
import com.undefined.lynx.sidebar.order
import com.undefined.lynx.sidebar.sidebar.line.*
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

@Suppress("UNUSED")
class Sidebar(
    title: Component,
    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    private val async: Boolean = false,
    kotlinDSL: Sidebar.() -> Unit = {}
) {

    internal val players: MutableList<Player> = mutableListOf()
    private val objective = NMSManager.nms.scoreboard.createObjective(scoreboard, title)
    private val lines: MutableList<Line> = mutableListOf()

    var updateTimerTick = 20

    init {
        kotlinDSL()
        ScoreboardManager.activeSidebars.add(this)
    }

    fun updateDynamicLines() = checkAsyncAndApply(async) {
        lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.sideBarTeam.team, players) }
    }

    fun setTitle(title: String) = setTitle(Component.text(title))

    fun setTitle(title: Component) = checkAsyncAndApply(async) {
        NMSManager.nms.scoreboard.setTitle(objective, title)
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 2, players)
    }

    fun addBlankLine() = addEmptyLine()

    fun addEmptyLine() = addLine(Component.empty())

    fun addLine(line: Component) {
        if (lines.size >= 15) return
        val orderId = nextOrderId()
        NMSManager.nms.scoreboard.sendSetScorePacket(orderId, Component.empty(), objective, 0, players)
        lines.add(Line(line, orderId))
    }


    fun removeLine(line: Component) = checkAsyncAndApply(async) {
        lines.firstOrNull { it.text == line }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket("${this.order}${this.text}", objective, players)
            lines.remove(this)
        }
    }

    fun removeDynamicLine(id: Any) = checkAsyncAndApply(async) {
        lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket(this.order, objective, players)
            lines.remove(this)
            if (this is TimerLine) this.task?.cancel()
        }
    }

    fun addStringDynamicPlayerLine(id: Any, run: Player.() -> String) = addDynamicPlayerLine(id) { Component.text(run(this)) }

    fun addDynamicPlayerLine(id: Any, run: Player.() -> Component) = checkAsyncAndApply(async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)

        for (player in players) {
            NMSManager.nms.scoreboard.setTeamPrefix(pair.first, run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, listOf(player))
        }

        NMSManager.nms.scoreboard.sendSetScorePacket(pair.second, Component.empty(), objective, 0, players)
        val line = PlayerLine(SidebarTeam(pair.first, id), pair.second, run)
        lines.add(line)
    }

    fun modifyDynamicPlayerLine(id: Any, run: Player.() -> Component) = checkAsyncAndApply(async) {
        val playerLine = lines.filterIsInstance<PlayerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        for (player in players) {
            NMSManager.nms.scoreboard.setTeamPrefix(playerLine.sideBarTeam.team, run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(playerLine.sideBarTeam.team, listOf(player))
        }
        playerLine.run = run
    }

    fun updateDynamicPlayerLine(id: Any) = updateDynamicPlayerLine(id, players)

    fun updateDynamicPlayerLine(id: Any, vararg player: Player) = updateDynamicPlayerLine(id, player.toList())

    fun updateDynamicPlayerLine(id: Any, toUpdate: List<Player>) = checkAsyncAndApply(async) {
        val players = toUpdate.filter { this.players.contains(it) }
        lines.filterIsInstance<PlayerLine>().firstOrNull { it.sideBarTeam.id == id }?.let { line ->
            for (player in players) {
                NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, line.run(player))
                NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, listOf(player))
            }
        }
    }

    fun addDynamicLine(id: Any, line: String) = addDynamicLine(id, Component.text(line))

    fun addDynamicLine(id: Any, line: Component) = checkAsyncAndApply(async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, players)
        NMSManager.nms.scoreboard.sendSetScorePacket(pair.second, Component.empty(), objective, 0, players)
        lines.add(TeamLine(line, SidebarTeam(pair.first, id), pair.second))
    }

    fun modifyDynamicLine(id: Any, line: String, update: Boolean = true) = modifyDynamicLine(id, Component.text(line), update)

    @JvmOverloads
    fun modifyDynamicLine(id: Any, line: Component, update: Boolean = true) = checkAsyncAndApply(async) {
        val teamLine = lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.setTeamPrefix(teamLine.sideBarTeam.team, line)
        teamLine.text = line
        if (update) NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(teamLine, players)
    }

    fun addStringDynamicTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = true, run: () -> String) = addDynamicTimerLine(id, ticks, async) { Component.text(run()) }

    @JvmOverloads
    fun addDynamicTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = true, run: () -> Component) = checkAsyncAndApply(this.async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        val line = StaticTimerLine(SidebarTeam(pair.first, id), pair.second, run, null)
        val runnable = Runnable {
            NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line.run())
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, players)
        }
        runnable.run()
        runDynamicTimer(runnable, pair.second, ticks, line)
        lines.add(line)
    }

    fun modifyStringDynamicTimerLine(id: Any, run: () -> String) = modifyDynamicTimerLine(id) { Component.text(run()) }

    fun modifyDynamicTimerLine(id: Any, run: () -> Component) = checkAsyncAndApply(async) {
        val line = lines.filterIsInstance<StaticTimerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        line.run = run
        NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, run())
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, players)
    }

    fun addStringDynamicPlayerTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = false, run: Player.() -> String) = addDynamicPlayerTimerLine(id, ticks, async) { Component.text(run(this)) }

    @JvmOverloads
    fun addDynamicPlayerTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = false, run: Player.() -> Component) {
        val pair = dynamicLineCheck(id) ?: return
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        val line = PlayerTimerLine(SidebarTeam(pair.first, id), pair.second, run, null)
        val runnable = Runnable {
            for (player in players) {
                NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line.run(player))
                NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, listOf(player))
            }
        }
        runnable.run()
        runDynamicTimer(runnable, pair.second, ticks, line)
        lines.add(line)
    }

    fun modifyDynamicPlayerTimerLine(id: Any, run: Player.() -> Component) = checkAsyncAndApply(async) {
        val line = lines.filterIsInstance<PlayerTimerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        line.run = run

        for (player in players) {
            NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, listOf(player))
        }
    }

    fun updateTimerTick(ticks: Int) = apply { updateTimerTick = ticks }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun addViewers(list: List<Player>) {
        val list = list.filter { !players.contains(it) }
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 0, list)
        NMSManager.nms.scoreboard.sendClientboundSetDisplayObjectivePacket(objective, list)

        for (line in lines) {
            when (line) {
                is PlayerLine -> {
                    for (player in players) {
                        NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, line.run(player))
                        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, listOf(player))
                    }
                }
                is TeamLine -> NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, players)
            }

            NMSManager.nms.scoreboard.sendSetScorePacket(line.order, line.text, objective, 0, list)
        }
        players.addAll(list)
    }

    fun removeViewer(player: Player) = removeViewers(listOf(player))

    fun removeViewers(list: List<Player>) = checkAsyncAndApply(async) {
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 1, list)
        lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(it.sideBarTeam.team, list) }
        players.removeAll(list)
    }

    fun remove() = checkAsyncAndApply(async) {
        for (line in lines) {
            when(line) {
                is TeamLine -> removeDynamicLine(line.sideBarTeam.id)
                else -> removeLine(line.text)
            }
        }
        lines.clear()
        removeViewers(players)
        ScoreboardManager.activeSidebars.remove(this)
    }

    private fun runDynamicTimer(runnable: Runnable, order: String, ticks: Int, line: TimerLine) {
        NMSManager.nms.scoreboard.sendSetScorePacket(order, Component.empty(), objective, 0, players)
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

fun sidebar(
    title: Component,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    async: Boolean = false,
    block: Sidebar.() -> Unit = {}
): Sidebar = Sidebar(title, scoreboard, async, block)