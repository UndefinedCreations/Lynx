package com.undefined.lynx.tab.size

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.tab.DefaultTabSkin
import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.tab.TabManager
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object TabSizeManager: Listener {

    private val players: MutableList<Player> = mutableListOf()

    private val activeServerPlayers: MutableList<Any> = mutableListOf()
    private val unactiveServerPlayers: MutableList<Any> = mutableListOf()

    private val team: Any = TabManager.createTeam(TabManager.order(999))

    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
        val order = TabManager.order(999)
        for (num in 0..80) {
            val serverPlayer = TabManager.createFakePlayer(
                order,
                DefaultTabSkin.TEXTURE,
                DefaultTabSkin.SIGN
            )
            activeServerPlayers.add(serverPlayer)
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
            TabManager.addFakePlayers(activeServerPlayers, playerList)
            TabManager.modifyTeamName(team, fakeNameJson, playerList)
            TabManager.modifyFakePlayerLatency(activeServerPlayers, latency.latency, playerList)
        },
        async
    )

    fun removePlayer(player: Player, async: Boolean = true) = runRunnable(
        {
            val playerList = listOf(player)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacketListServerPlayer(activeServerPlayers, playerList)
            NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(team, playerList)
        },
        async
    )


    private fun runRunnable(runnable: Runnable, async: Boolean) = if (async) Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, runnable) else runnable.run()

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable {
            if (activeServerPlayers.isEmpty()) return@Runnable
            val removePlayer = activeServerPlayers.first()
            unactiveServerPlayers.add(removePlayer)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacketListServerPlayer(listOf(removePlayer), players)
            activeServerPlayers.remove(removePlayer)
        })
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (Bukkit.getOnlinePlayers().size >= 80) return
        Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable {
            val addPlayer = unactiveServerPlayers.first()
            activeServerPlayers.add(addPlayer)
            TabManager.addFakePlayer(addPlayer, players)
            unactiveServerPlayers.remove(activeServerPlayers)
        })
    }

}