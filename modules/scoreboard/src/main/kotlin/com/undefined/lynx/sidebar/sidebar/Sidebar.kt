package com.undefined.lynx.sidebar.sidebar

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.sidebar.ScoreboardManager
import com.undefined.lynx.sidebar.checkAsyncAndApply
import com.undefined.lynx.sidebar.order
import com.undefined.lynx.sidebar.sidebar.line.*
import com.undefined.lynx.sidebar.toJson
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Scoreboard

@Suppress("UNUSED")
class Sidebar(
    title: String,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    kotlinDSL: Sidebar.() -> Unit = {}
): AbstractSideBar(title, scoreboard) {

    init {
        kotlinDSL()
        ScoreboardManager.activeSidebars.add(this)
    }

    fun updateDynamicLines(async: Boolean = false) = checkAsyncAndApply(async) {
        lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.sideBarTeam.team, players) }
    }
    fun setTitle(title: String) = apply { setTitleJson(title.toJson()) }
    fun setTitle(title: Component) = apply { setTitleJson(title.toJson()) }
    fun addBlankLine() = addEmptyLine()
    fun addEmptyLine() = addLine(Component.empty())
    fun addLine(line: Component) = apply { addLineWithJson(line.toJson()) }
    fun addLine(line: String) = apply { addLineWithJson(line.toJson()) }
    fun removeLine(line: Component) = apply { removeLineJson(line.toJson()) }
    fun removeLine(line: String) = apply { removeLineJson(line.toJson()) }
    fun removeDynamicLine(id: Any) = apply {
        lines.filterIsInstance<TeamLine>().firstOrNull { it.sideBarTeam.id == id }?.run {
            NMSManager.nms.scoreboard.sendClientboundResetScorePacket(this.order, objective, players)
            lines.remove(this)
            if (this is TimerLine) this.task?.cancel()
        }
    }
    fun addStringDynamicPlayerLine(id: Any, run: Player.() -> String) = addDynamicPlayerLineJson(id) { run(this).toJson() }
    fun addDynamicPlayerLine(id: Any, run: Player.() -> Component) = apply { addDynamicPlayerLineJson(id) { run(this).toJson() } }
    fun modifyStringDynamicPlayerLine(id: Any, run: Player.() -> String) = apply { modifyDynamicPlayerLineJson(id) { run(this).toJson() } }
    fun modifyDynamicPlayerLine(id: Any, run: Player.() -> Component) = apply { modifyDynamicPlayerLineJson(id) { run(this).toJson() } }
    fun updateStringDynamicPlayerLine(id: Any) = updateDynamicPlayerLine(id, players)
    fun updateDynamicPlayerLine(id: Any, vararg player: Player) = updateDynamicPlayerLine(id, player.toList())
    fun updateDynamicPlayerLine(id: Any, toUpdate: List<Player>) = apply {
        lines.filterIsInstance<PlayerLine>().firstOrNull { it.sideBarTeam.id == id }?.let { line ->
            for (player in toUpdate.filter { this.players.contains(it) }) for (player in players) updateTeamPrefix(line.sideBarTeam.team, line.run(player), listOf(player))
        }
    }
    fun addStringDynamicLine(id: Any, line: String) = apply { addDynamicLineJson(id, line.toJson()) }
    fun addDynamicLine(id: Any, line: Component) = apply { addDynamicLineJson(id, line.toJson()) }
    fun modifyStringDynamicLine(id: Any, line: String, update: Boolean = true) = apply { modifyDynamicLineJson(id, line.toJson(), update) }
    fun modifyDynamicLine(id: Any, line: Component, update: Boolean = true) = apply { modifyDynamicLineJson(id, line.toJson(), update) }
    fun addStringDynamicTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = true, run: () -> String) = apply { addDynamicTimerLineJson(id, ticks, async) { run().toJson() } }
    fun addDynamicTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = true, run: () -> Component) = apply { addDynamicTimerLineJson(id, ticks, async) { run().toJson() } }
    fun modifyStringDynamicTimerLine(id: Any, run: () -> String) = apply { modifyDynamicTimerLineJson(id) { run().toJson() } }
    fun modifyDynamicTimerLine(id: Any, run: () -> Component) = apply { modifyDynamicTimerLineJson(id) { run().toJson() } }
    fun addStringDynamicPlayerTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = false, run: Player.() -> String) = apply { addDynamicPlayerTimerLineJson(id, ticks, async) { run(this).toJson() } }
    fun addDynamicPlayerTimerLine(id: Any, ticks: Int = updateTimerTick, async: Boolean = false, run: Player.() -> Component) = apply { addDynamicPlayerTimerLineJson(id, ticks, async) { run(this).toJson() } }
    fun modifyDynamicPlayerTimerLine(id: Any, run: Player.() -> Component) = apply { modifyDynamicPlayerTimerLineJson(id) { run(this).toJson() } }
    fun modifyStringDynamicPlayerTimerLine(id: Any, run: Player.() -> String) = apply { modifyDynamicPlayerTimerLineJson(id) { run(this).toJson() } }
    fun updateTimerTick(ticks: Int) = apply { updateTimerTick = ticks }
    fun addViewer(player: Player) = addViewers(listOf(player))
    fun addViewers(list: List<Player>) {
        val list = list.filter { !players.contains(it) }
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 0, list)
        NMSManager.nms.scoreboard.sendClientboundSetDisplayObjectivePacket(objective, list)

        for (line in lines) {
            when (line) {
                is PlayerTimerLine -> for (player in players) updateTeamPrefix(line.sideBarTeam.team, line.run(player), listOf(player))
                is PlayerLine -> for (player in players) updateTeamPrefix(line.sideBarTeam.team, line.run(player), listOf(player))
                is TeamLine -> NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(line.sideBarTeam.team, players)
            }
            NMSManager.nms.scoreboard.sendSetScorePacket(line.order, line.text.toJson(), objective, 0, list)
        }
        players.addAll(list)
    }
    fun removeViewer(player: Player) = removeViewers(listOf(player))
    fun removeViewers(list: List<Player>) = apply {
        NMSManager.nms.scoreboard.sendClientboundSetObjectivePacket(objective, 1, list)
        lines.filterIsInstance<TeamLine>().forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(it.sideBarTeam.team, list) }
        players.removeAll(list)
    }
    fun remove() = apply {
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
    fun resend() = apply {
        val active = mutableListOf<Player>()
        active.addAll(players)
        removeViewers(players)
        addViewers(active)
    }
}

fun sidebar(
    title: String,
    scoreboard: Scoreboard = Bukkit.getScoreboardManager()!!.mainScoreboard,
    block: Sidebar.() -> Unit = {}
): Sidebar = Sidebar(title, scoreboard, block)
