package com.undefined.lynx.sidebar.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.ScoreboardManager
import com.undefined.lynx.sidebar.checkAsyncAndApply
import com.undefined.lynx.sidebar.order
import com.undefined.lynx.sidebar.sidebar.lines.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

@Suppress("unused")

class SideBar(
    title: String,
    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    private val async: Boolean = false,
    kotlinDSL: SideBar.() -> Unit = {}
) {

    internal val players: MutableList<Player> = mutableListOf()
    private val objective = NMSManager.nms.scoreboard.createObjective(scoreboard, title)
    private val lines: MutableList<Line> = mutableListOf()


    var updateTimerTick = 20

    init {
        kotlinDSL()
        ScoreboardManager.activeSideBars.add(this)
    }

    fun updateDynamicLines() = checkAsyncAndApply(async) {
        lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.sideBarTeam.team, players) }
    }

    fun setTitle(title: String) = checkAsyncAndApply(async) {
        NMSManager.nms.scoreboard.setTitle(objective, title)
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 2, players)
    }

    fun addEmptyLine() = checkAsyncAndApply(async) {
        if (lines.size >= 15) return@checkAsyncAndApply
        val orderID = nextOrderId()
        NMSManager.nms.scoreboard.sendScorePacket("$orderID ", objective, 0, players)
        lines.add(EmptyLine(" ", orderID))
    }

    fun addBlankLine() = addEmptyLine()

    fun addLine(line: String) = checkAsyncAndApply(async) {
        if (lines.size >= 15) return@checkAsyncAndApply
        val orderID = nextOrderId()
        NMSManager.nms.scoreboard.sendScorePacket("$orderID$line", objective, 0, players)
        lines.add(Line(line, orderID))
    }

    fun removeLine(line: String) = checkAsyncAndApply(async) {
        lines.firstOrNull { it.text == line }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket("${this.order}${this.text}", objective, players)
            lines.remove(this)
        }
    }

    fun removeDynamicLine(id: Any) = checkAsyncAndApply(async) {
        lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket(this.order, objective, players)
            lines.remove(this)
            if (this is TimerLine) this.bukkitTask?.cancel()
        }
    }

    fun addDynamicPlayerLine(id: Any, run: Player.() -> String) = checkAsyncAndApply(async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)

        players.forEach {
            NMSManager.nms.scoreboard.setTeamPrefix(pair.first, run(it))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, listOf(it))
        }

        NMSManager.nms.scoreboard.sendScorePacket(pair.second, objective, 0, players)
        val line = PlayerLine(SideBarTeam(pair.first, id), pair.second, run)
        lines.add(line)
    }

    fun modifyDynamicPlayerLine(id: Any, run: Player.() -> String) = checkAsyncAndApply(async) {
        val playerLine = lines.filterIsInstance<PlayerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        players.forEach {
            NMSManager.nms.scoreboard.setTeamPrefix(playerLine.sideBarTeam.team, run(it))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(playerLine.sideBarTeam.team, listOf(it))
        }
        playerLine.run = run
    }

    fun updateDynamicPlayerLine(id: Any) = updateDynamicPlayerLine(id, players)

    fun updateDynamicPlayerLine(id: Any, vararg player: Player) = updateDynamicPlayerLine(id, player.toList())

    fun updateDynamicPlayerLine(id: Any, toUpdate: List<Player>) = checkAsyncAndApply(async) {
        val toUpdate = toUpdate.filter { players.contains(it) }
        lines.filterIsInstance<PlayerLine>().firstOrNull { it.sideBarTeam.id == id }?.let { line ->
            toUpdate.forEach {
                NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, line.run(it))
                NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, listOf(it))
            }
        }
    }

    fun addDynamicLine(id: Any, line: String) = checkAsyncAndApply(async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, players)
        NMSManager.nms.scoreboard.sendScorePacket(pair.second, objective, 0, players)
        lines.add(TeamLine(line, SideBarTeam(pair.first, id), pair.second))
    }

    @JvmOverloads
    fun modifyDynamicLine(id: Any, line: String, update: Boolean = true) = checkAsyncAndApply(async) {
        val teamLine = lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.setTeamPrefix(teamLine.sideBarTeam.team, line)
        teamLine.text = line
        if (update) NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(teamLine, players)
    }

    @JvmOverloads
    fun addDynamicTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = true, run: Unit.() -> String) = checkAsyncAndApply(this.async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        val line = StaticTimerLine(SideBarTeam(pair.first, id), pair.second, run, null)
        val runnable = Runnable {
            NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line.run(Unit))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, players)
        }
        runnable.run()
        runDynamicTimer(runnable, pair.second, ticks, line)
        lines.add(line)
    }

    fun modifyDynamicTimerLine(id: Any, run: Unit.() -> String) = checkAsyncAndApply(async) {
        val line = lines.filterIsInstance<StaticTimerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        line.run = run
        NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, run(Unit))
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, players)
    }

    @JvmOverloads
    fun addDynamicPlayerTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = false, run: Player.() -> String) = checkAsyncAndApply(this.async) {
        val pair = dynamicLineCheck(id) ?: return@checkAsyncAndApply
        NMSManager.nms.scoreboard.addTeamEntry(pair.first, pair.second)
        val line = PlayerTimerLine(SideBarTeam(pair.first, id), pair.second, run, null)
        val runnable = Runnable {
            players.forEach {
                NMSManager.nms.scoreboard.setTeamPrefix(pair.first, line.run(it))
                NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(pair.first, listOf(it))
            }
        }
        runnable.run()
        runDynamicTimer(runnable, pair.second, ticks, line)
        lines.add(line)
    }

    fun modifyDynamicPlayerTimerLine(id: Any, run: Player.() -> String) = checkAsyncAndApply(async) {
        val line = lines.filterIsInstance<PlayerTimerLine>().firstOrNull { it.sideBarTeam.id == id } ?: return@checkAsyncAndApply
        line.run = run
        players.forEach {
            NMSManager.nms.scoreboard.setTeamPrefix(line.sideBarTeam.team, run(it))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, listOf(it))
        }
    }

    fun updateTimerTick(ticks: Int) = apply { updateTimerTick = ticks }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun addViewers(list: List<Player>) = checkAsyncAndApply(async) {
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
            }

            NMSManager.nms.scoreboard.sendScorePacket("${it.order}${if (it is TeamLine) "" else it.text}", objective, 0, list)
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
        lines.forEach {
            when(it) {
                is TeamLine -> removeDynamicLine(it.sideBarTeam.id)
                else -> removeLine(it.text)
            }
        }
        lines.clear()
        removeViewers(players)
        ScoreboardManager.activeSideBars.remove(this)
    }

    private fun runDynamicTimer(runnable: Runnable, order: String, ticks: Int, line: TimerLine) {
        NMSManager.nms.scoreboard.sendScorePacket(order, objective, 0, players)
        line.bukkitTask = if (async) Bukkit.getScheduler().runTaskTimerAsynchronously(LynxConfig.javaPlugin, runnable, ticks.toLong(), ticks.toLong())
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

    private fun orderToInt(string: String): Int {
        return string.split("§")[1].toCharArray()[0].lowercaseChar() - 'a'
    }

    private fun nextOrderId(): String {
        return order((lines.maxOfOrNull { orderToInt(it.order) } ?: 0) + 1)
    }

}

fun sidebar(
    title: String,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    async: Boolean = false,
    kotlinDSL: SideBar.() -> Unit = {}
): SideBar = SideBar(title, scoreboard, async, kotlinDSL)