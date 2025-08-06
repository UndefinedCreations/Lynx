package com.undefined.lynx.sidebar.sidebar.lines

import com.undefined.lynx.NMSManager
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.sidebar.sidebar.Sidebar
import com.undefined.lynx.util.toMiniMessageOrDefault
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class Line(text: String) : BasicLine() {

    private var text: String = text.toJson()

    constructor(text: Component) : this(text.toJson())

    override fun setUpLine(sidebar: Sidebar) {
        super.setUpLine(sidebar)
        NMSManager.nms.scoreboard.setTeamPrefix(team, text)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, sidebar.players)
    }

    fun setText(text: String, players: List<Player> = sideBar.players) {
        this.text = text.toMiniMessageOrDefault().toJson()
        NMSManager.nms.scoreboard.setTeamPrefix(team, this.text)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
    }

    fun setText(text: Component, players: List<Player> = sideBar.players) = apply {
        this.text = text.toJson()
        NMSManager.nms.scoreboard.setTeamPrefix(team, this.text)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, players)
    }

}