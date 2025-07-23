package com.undefined.lynx.nick

import com.undefined.lynx.GameProfile
import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object PlayerMetaManager : Listener {

    internal val trueGameProfile: HashMap<UUID, GameProfile> = hashMapOf()
    init {
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        trueGameProfile[event.player.uniqueId] = PlayerMetaUtil.getGameProfile(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        trueGameProfile.remove(event.player.uniqueId)
    }

    fun reloadPlayerMeta(player: Player) {
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoRemovePacket(listOf(player), listOf(player))
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoAddPacketPlayer(player, listOf(player))
        NMSManager.nms.nick.sendClientboundRespawnPacket(player)
        NMSManager.nms.playerMeta.sendClientboundPlayerInfoUpdateListedPacketPlayer(player, listOf(player))
        NMSManager.nms.nick.sendClientboundGameEventPacket(player)
        NMSManager.nms.nick.updateAbilities(player)

        player.health = player.health
        player.totalExperience = player.totalExperience
        player.teleport(player.location.clone())
        player.foodLevel = player.foodLevel
        player.updateInventory()
    }

    fun reloadPlayerMetaGlobal(player: Player) {
        for (player in Bukkit.getOnlinePlayers()) player.hidePlayer(LynxConfig.javaPlugin, player)
        Bukkit.getScheduler().runTaskLater(LynxConfig.javaPlugin, Runnable {
            for (player in Bukkit.getOnlinePlayers()) player.showPlayer(LynxConfig.javaPlugin, player)
        }, 1)
    }
}

