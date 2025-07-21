package com.undefined.lynx.sidebar.sidebar

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.adventure.toLegacyText
import com.undefined.lynx.sidebar.ScoreboardManager
import com.undefined.lynx.sidebar.order
import com.undefined.lynx.sidebar.sidebar.lines.*
import com.undefined.lynx.util.ReturnBlock
import com.undefined.lynx.util.RunBlock
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

@Suppress("UNUSED")
class Sidebar @JvmOverloads constructor(
    title: String,
    internal val scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    block: RunBlock<Sidebar> = RunBlock {}
) {

    internal val players: MutableList<Player> = mutableListOf()
    internal val objective = NMSManager.nms.scoreboard.createObjective(scoreboard, title.toJson())

    internal val lines: MutableList<BasicLine> = mutableListOf()

    @JvmOverloads constructor(title: Component, scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard, block: RunBlock<Sidebar> = RunBlock {}): this(title.toLegacyText(), scoreboard, block)

    init {
        block.run(this)
        ScoreboardManager.activeSidebars.add(this)
    }

    fun setTitle(title: String) {
        NMSManager.nms.scoreboard.setTitle(objective, title.toJson())
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 2, players)
    }

    fun setTitle(title: Component) {
        NMSManager.nms.scoreboard.setTitle(objective, title.toJson())
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 2, players)
    }

    fun addEmptyLine() = apply { addLine(Line("")) }
    fun addBlankLine() = addEmptyLine()

    fun addLine(line: BasicLine) = apply {
        if (lines.size >= 15) return@apply
        line.setUpLine(this)
        lines.add(line)
    }

    fun addUpdatableLine(run: () -> String) = UpdatableLine(run).apply { addLine(this) }
    fun addComponentUpdatableLine(run: () -> Component) = UpdatableLine().apply {
        setComponentUpdatable(run)
        addLine(this)
    }
    fun addUpdatablePlayerLine(run: ReturnBlock<Player, String>) = UpdatablePlayerLine(run).apply { addLine(this) }
    fun addComponentUpdatablePlayerLine(run: ReturnBlock<Player, Component>) = UpdatablePlayerLine().apply {
        setComponentUpdatable(run)
        addLine(this)
    }
    @JvmOverloads
    fun addUpdatablePlayerTimerLine(
        ticks: Int,
        async: Boolean = false,
        run: ReturnBlock<Player, String>
    ) = UpdatablePlayerTimerLine(ticks, async, run).apply { addLine(this) }
    @JvmOverloads
    fun addComponentUpdatablePlayerTimerLine(
        ticks: Int,
        async: Boolean = false,
        run: ReturnBlock<Player, Component>
    ) = UpdatablePlayerTimerLine(ticks, async).apply {
        setComponentUpdatable(run)
        addLine(this)
    }
    @JvmOverloads
    fun addUpdatableTimerLine(
        ticks: Int,
        async: Boolean = false,
        run: () -> String
    ) = UpdatableTimerLine(ticks, async, run).apply { addLine(this) }
    @JvmOverloads
    fun addComponentUpdatableTimerLine(
        ticks: Int,
        async: Boolean = false,
        run: () -> Component
    ) = UpdatableTimerLine(ticks, async).apply {
        setComponentUpdatable(run)
        addLine(this)
    }

    fun addViewers(playerList: List<Player>) = apply {
        players.addAll(playerList)
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 0, playerList)
        NMSManager.nms.scoreboard.sendClientboundSetDisplayObjectivePacket(objective, playerList)
        for (line in lines) line.addPlayers(playerList)
    }

    fun addViewer(player: Player) = addViewers(listOf(player))

    fun removeViewer(player: Player) = removeViewers(listOf(player))

    fun removeViewers(playerList: List<Player>) = apply {
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 1, playerList)
        for (line in lines) line.removePlayers(playerList)
        players.removeAll(playerList)
    }

    fun clear() = apply {
        removeViewers(players)
        lines.clear()
    }

    internal fun nextOrderId(): String = order(lines.size + 1)
}

fun sidebar(
    title: String,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    block: Sidebar.() -> Unit = {}
) = Sidebar(title, scoreboard, block)

fun sidebar(
    title: Component,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    block: Sidebar.() -> Unit = {}
) = sidebar(title.toLegacyText(), scoreboard, block)
