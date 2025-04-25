package com.undefined.lynx.nick

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import com.undefined.lynx.event.event
import com.undefined.lynx.scheduler.delay
import com.undefined.lynx.util.legacySectionString
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object NickManager {

    val trueNames: HashMap<UUID, String> = hashMapOf()
    val trueSkins: HashMap<UUID, Pair<String, String>> = hashMapOf()

    init {
        event<PlayerQuitEvent> {
            trueNames.remove(player.uniqueId)
            trueSkins.remove(player.uniqueId)
        }
    }

    fun setName(player: Player, name: String, reloadPlayer: Boolean) {
        if (!trueNames.containsKey(player.uniqueId)) {
            trueNames[player.uniqueId] = player.name
        } else if (trueNames[player.uniqueId] == name) {
            trueNames.remove(player.uniqueId)
        }
        NMSManager.nms.nick.setName(player, name)
        if (reloadPlayer) {
            reloadPlayerMeta(player)
            reloadPlayerMetaGlobal(player)
        }
    }

    fun setSkin(player: Player, texture: String, signature: String, reloadPlayer: Boolean) {
        if (!trueSkins.containsKey(player.uniqueId)) {
            trueSkins[player.uniqueId] = player.getSkin().let { Pair(it.texture, it.signature) }
        } else {
            val pair = trueSkins[player.uniqueId]!!
            if (pair.first == texture && pair.second == signature) {
                trueSkins.remove(player.uniqueId)
            }
        }
        NMSManager.nms.nick.setSkin(player, texture, signature)
        if (reloadPlayer) {
            reloadPlayerMeta(player)
            reloadPlayerMetaGlobal(player)
        }
    }

    fun reloadPlayerMeta(player: Player) {
        NMSManager.nms.nick.sendClientboundPlayerInfoRemovePacket(player)
        NMSManager.nms.nick.sendClientboundPlayerInfoAddPacket(player)
        NMSManager.nms.nick.sendClientboundRespawnPacket(player)
        NMSManager.nms.nick.sendClientboundPlayerInfoUpdateListedPacket(player)
        NMSManager.nms.nick.sendClientboundGameEventPacket(player)
        NMSManager.nms.nick.updateAbilities(player)

        player.health = player.health
        player.totalExperience = player.totalExperience
        player.teleport(player.location.clone())
        player.foodLevel = player.foodLevel
        player.updateInventory()
    }

    fun reloadPlayerMetaGlobal(player: Player) {
        Bukkit.getOnlinePlayers().forEach {
            it.hidePlayer(LynxConfig.javaPlugin, player)
        }
        delay(1) {
            Bukkit.getOnlinePlayers().forEach {
                it.showPlayer(LynxConfig.javaPlugin, player)
            }
        }
    }

}

fun Player.setName(name: String, reloadPlayer: Boolean = true) = NickManager.setName(this, name, reloadPlayer)

fun Player.setName(name: Component, reloadPlayer: Boolean = true) = setName(name.legacySectionString(), reloadPlayer)

fun Player.setSkin(skin: Skin, reloadPlayer: Boolean = true) = setSkin(skin.texture, skin.signature, reloadPlayer)

fun Player.setSkin(texture: String, signature: String, reloadPlayer: Boolean = true) = NickManager.setSkin(this, texture, signature, reloadPlayer)

/**
 * Resend entity meta to the player and only the player
 */
fun Player.reloadPlayerMeta() = NickManager.reloadPlayerMeta(this)

/**
 * Resend entity meta to all players.
 */
fun Player.reloadPlayerMetaGlobal() = NickManager.reloadPlayerMetaGlobal(this)

fun Player.getTrueName(): String = NickManager.trueNames[uniqueId] ?: name

fun Player.getSkin(): Skin = NMSManager.nms.nick.getSkin(this)

fun Player.getTrueSkin(): Skin = NickManager.trueSkins[uniqueId]?.let { Skin(it.first, it.second) } ?: getSkin()

fun Player.hasTrueName(): Boolean = !NickManager.trueNames.containsKey(uniqueId)

fun Player.hasTrueSkin(): Boolean = !NickManager.trueSkins.containsKey(uniqueId)

fun Player.resetName(reloadPlayer: Boolean = true) = NickManager.trueNames[uniqueId]?.let { setName(it, reloadPlayer) }

fun Player.resetSkin(reloadPlayer: Boolean = true) = NickManager.trueSkins[uniqueId]?.let { setSkin(it.first, it.second, reloadPlayer) }