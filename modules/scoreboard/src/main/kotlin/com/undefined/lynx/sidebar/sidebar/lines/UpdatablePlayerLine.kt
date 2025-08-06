package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.interfaces.Updatable
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.function.Function

class UpdatablePlayerLine @JvmOverloads constructor(run: Function<Player, String> = Function { "" }) : BasicLine(),
    Updatable<UpdatablePlayerLine> {

    private var jsonRun: Function<Player, String> = Function { run.apply(it).toMiniMessageOrDefault().toJson() }

    fun setUpdatable(run: Function<Player, String>) = apply {
        jsonRun = Function { run.apply(it).toMiniMessageOrDefault().toJson() }
        update()
    }

    fun setComponentUpdatable(run: Function<Player, Component>) = apply {
        jsonRun = Function { run.apply(it).toJson() }
        update()
    }

    override fun addPlayers(players: List<Player>) {
        super.addPlayers(players)
        for (player in players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.apply(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }

    override fun update() = apply {
        for (player in this.sideBar.players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.apply(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }
}