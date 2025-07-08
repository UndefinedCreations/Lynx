package com.undefined.lynx.tab.size

import com.undefined.lynx.NMSManager
import com.undefined.lynx.tab.DefaultTabSkin
import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.tab.TabManager
import com.undefined.lynx.tab.runRunnable
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.entity.Player

object TabSizeManager {

    private val players: MutableList<Player> = mutableListOf()

    private val serverPlayers: MutableList<Any> = mutableListOf()

    private val team: Any = TabManager.createTeam(TabManager.order(999))

    init {
        val order = TabManager.order(999)
        for (num in 0..80) {
            val serverPlayer = TabManager.createFakePlayer(
                order,
                DefaultTabSkin.TEXTURE,
                DefaultTabSkin.SIGN
            )
            serverPlayers.add(serverPlayer)
            TabManager.addTeamEntry(team, order)
        }
        TabManager.modifyTeamName(team, ComponentSerializer.toJson(TextComponent("")).toString(), players)
    }

    fun addPlayer(
        player: Player,
        fakeNameJson: String,
        latency: TabLatency,
        async: Boolean
    ) = runRunnable(
        {
            val playerList = listOf(player)
            TabManager.addFakePlayers(serverPlayers, playerList)
            TabManager.modifyTeamName(team, fakeNameJson, playerList)
            TabManager.modifyFakePlayerLatency(serverPlayers, latency.latency, playerList)
        },
        async
    )

    fun removePlayer(player: Player, async: Boolean = true) = runRunnable(
        {
            val playerList = listOf(player)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacketListServerPlayer(serverPlayers, playerList)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(team, playerList)
        },
        async
    )





}