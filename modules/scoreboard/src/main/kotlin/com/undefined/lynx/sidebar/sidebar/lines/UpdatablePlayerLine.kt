package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.interfaces.Updatable
import com.undefined.lynx.util.ReturnBlock
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class UpdatablePlayerLine @JvmOverloads constructor(run: ReturnBlock<Player, String> = ReturnBlock { "" }) : BasicLine(),
    Updatable<UpdatablePlayerLine> {

    private var jsonRun: ReturnBlock<Player, String> = ReturnBlock { run.run(it).toMiniMessageOrDefault().toJson() }

    fun setUpdatable(run: ReturnBlock<Player, String>) = apply {
        jsonRun = ReturnBlock { run.run(it).toMiniMessageOrDefault().toJson() }
        update()
    }

    fun setComponentUpdatable(run: ReturnBlock<Player, Component>) = apply {
        jsonRun = ReturnBlock { run.run(it).toJson() }
        update()
    }

    override fun addPlayers(players: List<Player>) {
        super.addPlayers(players)
        for (player in players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }

    override fun update() = apply {
        for (player in this.sideBar.players) {
            NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.run(player))
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, listOf(player))
        }
    }
}