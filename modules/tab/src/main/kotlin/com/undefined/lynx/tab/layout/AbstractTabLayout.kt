package com.undefined.lynx.tab.layout

import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.tab.TabManager
import com.undefined.lynx.tab.runRunnable
import org.bukkit.entity.Player

open class AbstractTabLayout(
    internal val async: Boolean = true,
    texture: String,
    sign: String,
    defaultText: String
) {

    internal val fakePlayers: HashMap<Int, Any> = hashMapOf()
    internal val viewers: MutableList<Player> = mutableListOf()
    internal val runnable: HashMap<Int, TabLayoutRunnable> = hashMapOf()
    private val teams: HashMap<Int, Any> = hashMapOf()

    init {
        for (x in 0..79) {
            val fakePlayer = TabManager.createFakePlayer(
                "",
                texture,
                sign
            )
            val name = TabManager.order(x)
            NMSManager.nms.playerMeta.setName(fakePlayer, name)
            fakePlayers[x] = fakePlayer
            val team = TabManager.createTeam(name)
            TabManager.addTeamEntry(team, name)
            NMSManager.nms.scoreboard.setTeamPrefix(team, defaultText)
            teams[x] = team
        }

        TabLayoutManager.activeTabLayout.add(this)
    }

    fun setTextJson(index: Int, string: String, view: List<Player> = viewers) {
        val team = teams[index] ?: throw IllegalArgumentException("Unable to find team of row ($index)")
        TabManager.modifyTeamName(team, string, view)
    }

    internal fun setTabSkin(index: Int, texture: String, sign: String, view: List<Player> = viewers) = runRunnable({
        val fakePlayer = fakePlayers[index] ?: throw IllegalArgumentException("Unable to find fake player of row ($index)")
        TabManager.modifyFakePlayerSkin(fakePlayer, texture, sign, view)
    }, async)

    internal fun setTabLatency(index: Int, latency: TabLatency, view: List<Player> = viewers) = runRunnable({
        val fakePlayer = fakePlayers[index] ?: throw IllegalArgumentException("Unable to find fake player of row ($index)")
        TabManager.modifyFakePlayerLatency(fakePlayer, latency.latency, view)
    }, async)

    fun addPlayer(player: Player) = addPlayers(listOf(player))

    fun addPlayers(players: List<Player>) = runRunnable({
        val players = players.filter { !players.contains(it) }
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(TabLayoutManager.badTeam, players)
        TabManager.addFakePlayers(fakePlayers.values.toList(), players)
        players.forEach { player ->
            TabLayoutManager.activeTabLayout.firstOrNull { it.viewers.contains(player) }?.let { removePlayer(player) }

            runnable.entries.forEach { map ->
                val team = teams[map.key] ?: return@runRunnable
                val fakePlayer = fakePlayers[map.key] ?: return@runRunnable
                map.value.run {
                    text?.run { NMSManager.nms.scoreboard.setTeamPrefix(team, this(player)) }
                    skin?.run { skin!!(player).let { TabManager.modifyFakePlayerSkin(fakePlayer, it.texture, it.signature, listOf(player)) } }
                    tabLatency?.run { TabManager.modifyFakePlayerLatency(fakePlayer, tabLatency!!(player).latency, listOf(player)) }
                }
            }

            teams.forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(it.value, listOf(player)) }
        }
        viewers.addAll(players)
    }, async)!!

    fun removePlayers(players: List<Player>) = runRunnable({
        viewers.removeAll(players)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(TabLayoutManager.badTeam, players)
        teams.forEach { NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketRemove(it.value, players) }
        fakePlayers.forEach { TabManager.removeFakePlayer(it.value, players) }
    }, async)!!

    fun removePlayer(player: Player) = removePlayers(listOf(player))

    fun remove() {
        removePlayers(viewers)
        fakePlayers.clear()
        teams.clear()
        runnable.clear()
        TabLayoutManager.activeTabLayout.remove(this)
    }

}

data class TabLayoutRunnable(
    var text: (Player.() -> String)?,
    var skin: (Player.() -> Skin)?,
    var tabLatency: (Player.() -> TabLatency)?
) {

    companion object {
        fun EMPTY(): TabLayoutRunnable = TabLayoutRunnable(null, null, null)
    }

}