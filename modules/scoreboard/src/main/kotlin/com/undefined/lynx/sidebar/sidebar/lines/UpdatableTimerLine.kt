package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.sidebar.sidebar.interfaces.TimerLine
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.scheduler.BukkitRunnable
import java.util.function.Supplier

class UpdatableTimerLine @JvmOverloads constructor(
    private val ticks: Int,
    private val async: Boolean = false,
    run: Supplier<String> = Supplier { "" }
): BasicLine(), TimerLine<UpdatableTimerLine> {

    private var jsonRun: Supplier<String> = Supplier { run.get().toMiniMessageOrDefault().toJson() }
    private var bukkitTask = object : BukkitRunnable() {
        override fun run() {
            update()
        }
    }

    private var isRunning: Boolean = false

    fun setUpdatable(run: Supplier<String>) = apply {
        jsonRun = Supplier { run.get().toMiniMessageOrDefault().toJson() }
    }

    fun setComponentUpdatable(run: Supplier<Component>) = apply {
        jsonRun = Supplier { run.get().toJson() }
    } 

    override fun setUpLine(sidebar: Sidebar) {
        super.setUpLine(sidebar)
        start(ticks, async)
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
        NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.get())
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, this.sideBar.players)
    }
}