package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.sidebar.sidebar.interfaces.Updatable
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component

class UpdatableLine @JvmOverloads constructor(run: () -> String = { "" }) : BasicLine(), Updatable<UpdatableLine> {

    private var jsonRun: () -> String

    init {
        jsonRun = { run.invoke().toMiniMessageOrDefault().toJson() }
    }

    fun setUpdatable(run: () -> String) = apply {
        jsonRun = { run.invoke().toMiniMessageOrDefault().toJson() }
        update()
    }

    fun setComponentUpdatable(run: () -> Component) = apply {
        jsonRun = { run.invoke().toJson() }
        update()
    }

    override fun update() = apply {
        NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.invoke())
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, this.sideBar.players)
    }

    override fun setUpLine(sidebar: Sidebar) {
        super.setUpLine(sidebar)
        NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.invoke())
    }
}