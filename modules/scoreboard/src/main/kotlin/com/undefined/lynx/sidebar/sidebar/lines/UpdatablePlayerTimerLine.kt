package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.interfaces.TimerLine
import com.undefined.lynx.util.ReturnBlock
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class UpdatablePlayerTimerLine @JvmOverloads constructor(
    ticks: Int,
    async: Boolean = false,
    run: ReturnBlock<Player, String> = ReturnBlock { "" }
): BasicLine(), TimerLine<UpdatablePlayerTimerLine> {

    private var jsonRun: ReturnBlock<Player, String> = ReturnBlock<Player, String> { run.run(it).toMiniMessageOrDefault().toJson() }
    private var bukkitTask = object : BukkitRunnable() {
        override fun run() {
            update()
        }
    }

    private var isRunning: Boolean = false

    init {
        start(ticks, async)
    }

    fun setUpdatable(run: ReturnBlock<Player, String>) = apply {
        jsonRun = ReturnBlock { run.run(it).toMiniMessageOrDefault().toJson() }
    }

    fun setComponentUpdatable(run: ReturnBlock<Player, Component>) = apply {
        jsonRun = ReturnBlock { run.run(it).toJson() }
    }

    override fun stop(): UpdatablePlayerTimerLine = apply {
        if (isRunning) {
            bukkitTask.cancel()
            isRunning = !isRunning
        }
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
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }

    override fun update(): UpdatablePlayerTimerLine = apply {
        for (player in this.sideBar.players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }

}