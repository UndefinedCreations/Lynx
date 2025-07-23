package com.undefined.lynx.nick

import com.google.gson.JsonParser
import com.undefined.lynx.GameProfile
import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import org.bukkit.entity.Player
import java.util.*

object PlayerMetaUtil {

    @JvmStatic
    @JvmOverloads
    fun setName(player: Player, name: String, reloadPlayer: Boolean = true) {
        NMSManager.nms.nick.setName(player, name)
        if (reloadPlayer) {
            PlayerMetaManager.reloadPlayerMeta(player)
            PlayerMetaManager.reloadPlayerMetaGlobal(player)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun setSkin(player: Player, skin: Skin, reloadPlayer: Boolean = true) {
        NMSManager.nms.nick.setSkin(player, skin.texture, skin.signature)
        if (reloadPlayer) {
            PlayerMetaManager.reloadPlayerMeta(player)
            PlayerMetaManager.reloadPlayerMetaGlobal(player)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun setSkin(player: Player, texture: String, signature: String, reloadPlayer: Boolean = true) = setSkin(player, Skin(texture, signature), reloadPlayer)

    @JvmStatic
    fun reloadPlayerMeta(player: Player) = PlayerMetaManager.reloadPlayerMeta(player)

    @JvmStatic
    fun reloadPlayerMetaGlobal(player: Player) = PlayerMetaManager.reloadPlayerMetaGlobal(player)

    @JvmStatic
    fun getTruePlayerName(player: Player): String = PlayerMetaManager.trueGameProfile[player.uniqueId]?.name ?: player.name

    @JvmStatic
    fun getPlayerSkin(player: Player): Skin = NMSManager.nms.nick.getSkin(player)

    @JvmStatic
    fun getTruePlayerSkin(player: Player): Skin = PlayerMetaManager.trueGameProfile[player.uniqueId]?.skin ?: getPlayerSkin(player)

    @JvmStatic
    fun isNameNicked(player: Player): Boolean = PlayerMetaManager.trueGameProfile[player.uniqueId]?.name != getTruePlayerName(player)

    @JvmStatic
    fun isSkinNicked(player: Player): Boolean = PlayerMetaManager.trueGameProfile[player.uniqueId]?.skin != getPlayerSkin(player)

    @JvmStatic
    @JvmOverloads
    fun resetName(player: Player, reloadPlayer: Boolean = true) = PlayerMetaManager.trueGameProfile[player.uniqueId]?.let { setName(player, it.name, reloadPlayer) }

    @JvmStatic
    @JvmOverloads
    fun resetSkin(player: Player, reloadPlayer: Boolean = true) = PlayerMetaManager.trueGameProfile[player.uniqueId]?.let { setSkin(player, it.skin, reloadPlayer) }

    @JvmStatic
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

    @JvmStatic
    fun getGameProfile(player: Player): GameProfile = GameProfile(player.name, player.getSkin())
    @JvmStatic
    fun hasModifiedGameProfile(player: Player): Boolean = PlayerMetaManager.trueGameProfile[player.uniqueId] == getGameProfile(player)

    @JvmStatic
    @JvmOverloads
    fun setGameProfile(player: Player, gameProfile: GameProfile, reloadPlayer: Boolean = true) {
        if (player.name != gameProfile.name) setName(player, gameProfile.name, reloadPlayer)
        if (player.getSkin() != gameProfile.skin) setSkin(player, gameProfile.skin, reloadPlayer)
    }
}

fun Player.getGameProfile(): GameProfile = PlayerMetaUtil.getGameProfile(this)

fun Player.hasModifiedGameProfile(): Boolean = PlayerMetaUtil.hasModifiedGameProfile(this)

fun Player.setGameProfile(gameProfile: GameProfile, reloadPlayer: Boolean = true) = PlayerMetaUtil.setGameProfile(this, gameProfile, reloadPlayer)

fun Player.setClientCape(cape: Cape) = PlayerMetaUtil.setClientSideCape(this, cape)

fun Player.setName(name: String, reloadPlayer: Boolean = true) = PlayerMetaUtil.setName(this, name, reloadPlayer)

fun Player.setSkin(skin: Skin, reloadPlayer: Boolean = true) = setSkin(skin.texture, skin.signature, reloadPlayer)

fun Player.setSkin(texture: String, signature: String, reloadPlayer: Boolean = true) =
    PlayerMetaUtil.setSkin(this, texture, signature, reloadPlayer)

/**
 * Resend entity meta to the player and only the player
 */
fun Player.reloadPlayerMeta() = PlayerMetaUtil.reloadPlayerMeta(this)

/**
 * Resend entity meta to all players.
 */
fun Player.reloadPlayerMetaGlobal() = PlayerMetaUtil.reloadPlayerMetaGlobal(this)

fun Player.getTrueName(): String = PlayerMetaUtil.getTruePlayerName(this)

fun Player.getSkin(): Skin = PlayerMetaUtil.getPlayerSkin(this)

fun Player.getTrueSkin(): Skin = PlayerMetaUtil.getTruePlayerSkin(this)

fun Player.isNameNicked(): Boolean = PlayerMetaUtil.isNameNicked(this)

fun Player.isSkinNicked(): Boolean = PlayerMetaUtil.isSkinNicked(this)

fun Player.resetName(reloadPlayer: Boolean = true) = PlayerMetaUtil.resetName(this, reloadPlayer)

fun Player.resetSkin(reloadPlayer: Boolean = true) = PlayerMetaUtil.resetSkin(this, reloadPlayer)