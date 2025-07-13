package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.interfaces.TimerLine
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

class UpdatableTimerLine @JvmOverloads constructor(
    ticks: Int,
    async: Boolean = false,
    run: () -> String = { "" }
): BasicLine(), TimerLine<UpdatableTimerLine> {

    private var jsonRun: () -> String = { run.invoke().toMiniMessageOrDefault().toJson() }
    private var bukkitTask = object : BukkitRunnable() {
        override fun run() {
            update()
        }
    }

    private var isRunning: Boolean = false

    init {
        start(ticks, async)
    }

    fun setUpdatable(run: () -> String) = apply {
        jsonRun = { run.invoke().toMiniMessageOrDefault().toJson() }
    }

    fun setComponentUpdatable(run: () -> Component) = apply {
        jsonRun = { run.invoke().toJson() }
    }

    override fun stop(): UpdatableTimerLine = apply {
        if (isRunning) {
            bukkitTask.cancel()
            isRunning = !isRunning
        }
    }

    override fun start(
        ticks: Int,
        async: Boolean
    ): UpdatableTimerLine = apply {
        stop()
        if (async)
            bukkitTask.runTaskTimerAsynchronously(LynxConfig.javaPlugin, 0, ticks.toLong())
        else
            bukkitTask.runTaskTimer(LynxConfig.javaPlugin, 0, ticks.toLong())
        isRunning = true
    }

    override fun update(): UpdatableTimerLine = apply {
        NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.invoke())
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, this.sideBar.players)
    }
}