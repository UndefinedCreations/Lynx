package com.undefined.lynx.nick

import com.google.gson.JsonParser
import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import com.undefined.lynx.nick.NickManager.trueNames
import com.undefined.lynx.nick.NickManager.trueSkins
import org.bukkit.entity.Player
import java.util.Base64
import kotlin.collections.set

object NickUtil {

    @JvmOverloads
    fun setName(player: Player, name: String, reloadPlayer: Boolean = true) {
        addOrRemove(trueNames, player.uniqueId, name)
        NMSManager.nms.nick.setName(player, name)
        if (reloadPlayer) {
            NickManager.reloadPlayerMeta(player)
            NickManager.reloadPlayerMetaGlobal(player)
        }
    }

    @JvmOverloads
    fun setSkin(player: Player, skin: Skin, reloadPlayer: Boolean = true) {
        addOrRemove(trueSkins, player.uniqueId, skin)
        NMSManager.nms.nick.setSkin(player, skin.texture, skin.signature)
        if (reloadPlayer) {
            NickManager.reloadPlayerMeta(player)
            NickManager.reloadPlayerMetaGlobal(player)
        }
    }

    @JvmOverloads
    fun setSkin(player: Player, texture: String, signature: String, reloadPlayer: Boolean = true) = setSkin(player, Skin(texture, signature), reloadPlayer)

    private fun <T, U> addOrRemove(map: HashMap<T, U>, key: T, value: U) {
        if (!map.containsKey(key)) {
            map[key] = value
        } else if (map[key] == value) {
            map.remove(key)
        }
    }


    fun reloadPlayerMeta(player: Player) = NickManager.reloadPlayerMeta(player)

    fun reloadPlayerMetaGlobal(player: Player) = NickManager.reloadPlayerMetaGlobal(player)

    fun getTruePlayerName(player: Player): String = trueNames[player.uniqueId] ?: player.name

    fun getPlayerSkin(player: Player): Skin = NMSManager.nms.nick.getSkin(player)

    fun getTruePlayerSkin(player: Player): Skin = trueSkins[player.uniqueId] ?: getPlayerSkin(player)

    fun isNameNicked(player: Player): Boolean = trueNames.containsKey(player.uniqueId)

    @JvmOverloads
    fun resetName(player: Player, reloadPlayer: Boolean = true) = trueNames[player.uniqueId]?.let { setName(player, it, reloadPlayer) }

    @JvmOverloads
    fun resetSkin(player: Player, reloadPlayer: Boolean = true) = trueSkins[player.uniqueId]?.let { setSkin(player, it, reloadPlayer) }

    fun setClientSideCape(player: Player, cape: Cape) {
        val currentSkin = getPlayerSkin(player)
        val json = JsonParser.parseString(String(Base64.getDecoder().decode(currentSkin.texture))).asJsonObject
        val capeJson = json.get("textures").asJsonObject.get("CAPE").asJsonObject
        val modifiedJson = capeJson.apply { addProperty("url", cape.texture) }
        val modifiedTextureJson = json.get("textures").asJsonObject.apply { add("CAPE", modifiedJson) }
        val finalJson = json.apply { add("textures", modifiedTextureJson) }
        setSkin(player, Base64.getEncoder().encodeToString(finalJson.toString().toByteArray()), currentSkin.signature, false)
        reloadPlayerMeta(player)
    }
}


fun Player.setClientCape(cape: Cape) = NickUtil.setClientSideCape(this, cape)

fun Player.setName(name: String, reloadPlayer: Boolean = true) = NickUtil.setName(this, name, reloadPlayer)

fun Player.setSkin(skin: Skin, reloadPlayer: Boolean = true) = setSkin(skin.texture, skin.signature, reloadPlayer)

fun Player.setSkin(texture: String, signature: String, reloadPlayer: Boolean = true) =
    NickUtil.setSkin(this, texture, signature, reloadPlayer)

/**
 * Resend entity meta to the player and only the player
 */
fun Player.reloadPlayerMeta() = NickUtil.reloadPlayerMeta(this)

/**
 * Resend entity meta to all players.
 */
fun Player.reloadPlayerMetaGlobal() = NickUtil.reloadPlayerMetaGlobal(this)

fun Player.getTrueName(): String = NickUtil.getTruePlayerName(this)

fun Player.getSkin(): Skin = NickUtil.getPlayerSkin(this)

fun Player.getTrueSkin(): Skin = NickUtil.getTruePlayerSkin(this)

fun Player.hasTrueName(): Boolean = !trueNames.containsKey(uniqueId)

fun Player.hasTrueSkin(): Boolean = !trueSkins.containsKey(uniqueId)

fun Player.resetName(reloadPlayer: Boolean = true) = NickUtil.resetName(this, reloadPlayer)

fun Player.resetSkin(reloadPlayer: Boolean = true) = NickUtil.resetSkin(this, reloadPlayer)