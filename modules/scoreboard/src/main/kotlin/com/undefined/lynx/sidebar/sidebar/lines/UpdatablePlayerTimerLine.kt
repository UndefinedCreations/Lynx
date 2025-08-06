package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.sidebar.sidebar.interfaces.TimerLine
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Function

class UpdatablePlayerTimerLine @JvmOverloads constructor(
    private val ticks: Int,
    private val async: Boolean = false,
    run: Function<Player, String> = Function { "" }
): BasicLine(), TimerLine<UpdatablePlayerTimerLine> {

    private var jsonRun: Function<Player, String> = Function<Player, String> { run.apply(it).toMiniMessageOrDefault().toJson() }
    private var bukkitTask = object : BukkitRunnable() {
        override fun run() {
            update()
        }
    }

    private var isRunning: Boolean = false

    fun setUpdatable(run: Function<Player, String>) = apply {
        jsonRun = Function { run.apply(it).toMiniMessageOrDefault().toJson() }
    }

    fun setComponentUpdatable(run: Function<Player, Component>) = apply {
        jsonRun = Function { run.apply(it).toJson() }
    }

    override fun stop(): UpdatablePlayerTimerLine = apply {
        if (isRunning) {
            bukkitTask.cancel()
            isRunning = !isRunning
        }
    }

    override fun setUpLine(sidebar: Sidebar) {
        super.setUpLine(sidebar)
        start(ticks, async)
    }

    override fun start(
        ticks: Int,
        async: Boolean
    ): UpdatablePlayerTimerLine = apply {
        stop()
        if (async)
            bukkitTask.runTaskTimerAsynchronously(LynxConfig.javaPlugin, 0, ticks.toLong())
        else
            bukkitTask.runTaskTimer(LynxConfig.javaPlugin, 0, ticks.toLong())
        isRunning = true
    }

    override fun addPlayers(players: List<Player>) {
        super.addPlayers(players)
        for (player in this.sideBar.players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.apply(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }

    override fun update(): UpdatablePlayerTimerLine = apply {
        for (player in this.sideBar.players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.apply(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }

}