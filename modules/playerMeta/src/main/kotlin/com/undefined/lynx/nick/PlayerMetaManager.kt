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

/**
 * This class is the manager for the players meta.
 *
 */
object PlayerMetaManager : Listener {

    /**
     * This variable contains all the original game-profiles of the player.
     * This is used when resting the players game-profile
     */
    internal val trueGameProfile: HashMap<UUID, GameProfile> = hashMapOf()
    /**
     * This variable contains all the modifiable game-profiles of the player.
     */
    internal val modifiedGameProfile: HashMap<UUID, GameProfile> = hashMapOf()

    init {
        for (player in Bukkit.getOnlinePlayers()) {
            val gameProfile = GameProfile(player.name, NMSManager.nms.nick.getSkin(player))
            trueGameProfile[player.uniqueId] = gameProfile
            modifiedGameProfile[player.uniqueId] = gameProfile.clone()
        }
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val gameProfile = GameProfile(event.player.name, NMSManager.nms.nick.getSkin(event.player))
        trueGameProfile[event.player.uniqueId] = gameProfile
        modifiedGameProfile[event.player.uniqueId] = gameProfile.clone()
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        trueGameProfile.remove(event.player.uniqueId)
    }

    /**
     * This method will reload the player for the client. It is used to change that player skin for the client
     *
     * @param player The client to reload
     */
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

    /**
     * This method will reload the player meta for all other clients.
     *
     * @param changePlayer The player to reload
     */
    fun reloadPlayerMetaGlobal(changePlayer: Player) {
        for (player in Bukkit.getOnlinePlayers().filter { it.world == changePlayer.world }) {
            player.hidePlayer(LynxConfig.javaPlugin, changePlayer)
        }
        Bukkit.getScheduler().runTaskLater(LynxConfig.javaPlugin, Runnable {
            for (player in Bukkit.getOnlinePlayers().filter { it.world == changePlayer.world }) {
                player.showPlayer(LynxConfig.javaPlugin, changePlayer)
            }
        }, 2)
    }

    fun starUp() {}
}

