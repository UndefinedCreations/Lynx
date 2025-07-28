package com.undefined.lynx.tab

import com.undefined.lynx.NMSManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

object TabManager {

    fun removePlayer(player: Player, players: List<Player>) = removePlayers(listOf(player), players)

    fun removePlayers(players: List<Player>, viewers: List<Player>) = NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacket(players, viewers)

    fun addPlayers(players: List<Player>, viewers: List<Player>) {
        for (player in players) {
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoAddPacketPlayer(player, viewers)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateListedPacketPlayer(player, viewers)
        }
    }

    fun addPlayer(player: Player, viewers: List<Player>) = addPlayers(listOf(player), viewers)

    fun createFakePlayer(name: String, texture: String, sign: String) = NMSManager.nms.npc.createServerPlayer(name, texture, sign)

    fun addFakePlayers(players: List<Any>, viewers: List<Player>) {
        for (player in players) {
            println("Fake Player")
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoAddPacket(player, viewers)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateListedPacket(player, viewers)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateListedOrderPacket(player, viewers)
        }
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateLatencyPacket(players, viewers)
    }

    fun createTeam(order: String) = NMSManager.nms.scoreboard.createTeam(Bukkit.getScoreboardManager()!!.mainScoreboard, "$order${UUID.randomUUID()}")

    fun addTeamEntry(team: Any, entry: String) = NMSManager.nms.scoreboard.addTeamEntry(team, entry)

    fun removeTeamEntry(team: Any, entry: String) = NMSManager.nms.scoreboard.removeTeamEntry(team, entry)

    fun modifyTeamName(team: Any, nameJson: String, viewers: List<Player>) {
        NMSManager.nms.scoreboard.setTeamPrefix(team, nameJson)
        NMSManager.nms.scoreboard.sendClientboundSetPlayerTeamPacketAddOrModify(team, viewers)
    }

    fun modifyFakePlayerLatency(players: List<Any>, latency: Int, viewers: List<Player>) {
        for (player in players) {
            NMSManager.nms.playerMeta.setLatency(player, latency)
            NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateListedPacket(player, viewers)
        }
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateLatencyPacket(players, viewers)
    }

    fun modifyFakePlayerLatency(player: Any, latency: Int, viewers: List<Player>) = modifyFakePlayerLatency(listOf(player), latency, viewers)

    fun addFakePlayer(player: Any, viewers: List<Player>) = addFakePlayers(listOf(player), viewers)

    fun modifyFakePlayerName(player: Any, name: String, viewers: List<Player>) {
        NMSManager.nms.playerMeta.setName(player, name)
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateListedPacket(player, viewers)
    }

    fun modifyFakePlayerSkin(player: Any, texture: String, sign: String, viewers: List<Player>) = modifyFakePlayersSkin(listOf(player), texture, sign, viewers)

    fun modifyFakePlayersSkin(players: List<Any>, texture: String, sign: String, viewers: List<Player>) {
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacketListServerPlayer(players, viewers)
        for (player in players) NMSManager.nms.playerMeta.setSkin(player, texture, sign)
        addFakePlayers(players, viewers)
    }

    fun removeFakePlayer(player: Any, viewers: List<Player>) {
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacketListServerPlayer(listOf(player), viewers)
    }

    fun order(index: Int): String =  "§${(index.toChar().code + 1).toChar()}§${(index.toChar().code + 1).toChar()}"

}

internal fun runRunnable(runnable: Runnable, async: Boolean) {
    if (async) CompletableFuture.supplyAsync { runnable.run() } else runnable.run()
}