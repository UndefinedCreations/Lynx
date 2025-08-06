package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.sidebar.sidebar.interfaces.Updatable
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import java.util.function.Supplier

class UpdatableLine @JvmOverloads constructor(run: Supplier<String> = Supplier { "" }) : BasicLine(), Updatable<UpdatableLine> {

    private var jsonRun: Supplier<String>

    init {
        jsonRun = Supplier{ run.get().toMiniMessageOrDefault().toJson() }
    }

    fun setUpdatable(run: Supplier<String>) = apply {
        jsonRun = Supplier{ run.get().toMiniMessageOrDefault().toJson() }
        update()
    }

    fun setComponentUpdatable(run: Supplier<Component>) = apply {
        jsonRun = Supplier{ run.get().toJson() }
        update()
    }

    override fun update() = apply {
        NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.get())
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, this.sideBar.players)
    }

    override fun setUpLine(sidebar: Sidebar) {
        super.setUpLine(sidebar)
        NMSManager.nms.scoreboard.setTeamPrefix(team, jsonRun.get())
    }
}