package com.undefined.lynx.nick

import com.google.gson.JsonParser
import com.undefined.lynx.GameProfile
import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import com.undefined.lynx.nick.events.PlayerCapeChangeEvent
import com.undefined.lynx.nick.events.PlayerNameChangeEvent
import com.undefined.lynx.nick.events.PlayerSkinChangeEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * This class is used to get and modify a player meta
 */
object PlayerMetaUtil {


    init {

        PlayerMetaManager.starUp()

    }

    /**
     * Changes the player username
     *
     * @param player The player to change
     * @param name The new name
     * @param reloadPlayer If it should reload the player from all clients
     */
    @JvmStatic
    @JvmOverloads
    fun setName(player: Player, name: String, reloadPlayer: Boolean = true) {
        val event = PlayerNameChangeEvent(player, player.name, name)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return
        NMSManager.nms.nick.setName(player, name)
        PlayerMetaManager.modifiedGameProfile[player.uniqueId]!!.name = name
        if (reloadPlayer) {
            PlayerMetaManager.reloadPlayerMeta(player)
            PlayerMetaManager.reloadPlayerMetaGlobal(player)
        }
    }

    /**
     * Changes the player skin
     *
     * @param player The player to change
     * @param skin The new skin
     * @param reloadPlayer If it should reload the player from all clients
     */
    @JvmStatic
    @JvmOverloads
    fun setSkin(player: Player, skin: Skin, reloadPlayer: Boolean = true) {
        val event = PlayerSkinChangeEvent(player, player.getGameProfile().skin, skin)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return
        NMSManager.nms.nick.setSkin(player, skin.texture, skin.signature)
        PlayerMetaManager.modifiedGameProfile[player.uniqueId]!!.apply {
            this.skin.texture = skin.texture
            this.skin.signature = skin.signature
        }
        if (reloadPlayer) {
            PlayerMetaManager.reloadPlayerMeta(player)
            PlayerMetaManager.reloadPlayerMetaGlobal(player)
        }
    }

    /**
     * Changes the player skin
     *
     * @param player The player to change
     * @param texture The skin texture
     * @param signature The skin signature
     * @param reloadPlayer If it should reload the player from all clients
     */
    @JvmStatic
    @JvmOverloads
    fun setSkin(player: Player, texture: String, signature: String, reloadPlayer: Boolean = true) = setSkin(player, Skin(texture, signature), reloadPlayer)

    /**
     * Reload the player for the client. It is used to change that player skin for the client
     *
     * @param player The client to reload
     */
    @JvmStatic
    fun reloadPlayerMeta(player: Player) = PlayerMetaManager.reloadPlayerMeta(player)

    /**
     * Reload the player meta for all other clients.
     *
     * @param player The player to reload
     */
    @JvmStatic
    fun reloadPlayerMetaGlobal(player: Player) = PlayerMetaManager.reloadPlayerMetaGlobal(player)

    /**
     * Get the original name of the player
     *
     * @param player Player to get the name from
     * @return The original player name
     */
    @JvmStatic
    fun getOriginalPlayerName(player: Player): String = PlayerMetaManager.trueGameProfile[player.uniqueId]?.name ?: player.name

    /**
     * Get the current player skin
     *
     * @param player The player to get the skin from
     * @return The current player skin
     */
    @JvmStatic
    fun getPlayerSkin(player: Player): Skin = getGameProfile(player).skin

    /**
     * Get the original player skin
     *
     * @param player The player to get the skin from
     * @return The original player skin
     */
    @JvmStatic
    fun getOriginalPlayerSkin(player: Player): Skin = PlayerMetaManager.trueGameProfile[player.uniqueId]?.skin ?: getPlayerSkin(player)

    /**
     * Check if the player name is the original
     */
    @JvmStatic
    fun isNameModified(player: Player): Boolean = PlayerMetaManager.trueGameProfile[player.uniqueId]?.name != getOriginalPlayerName(player)
    /**
     * Check if the player skin is the original
     */
    @JvmStatic
    fun isSkinModified(player: Player): Boolean = PlayerMetaManager.trueGameProfile[player.uniqueId]?.skin != getPlayerSkin(player)

    /**
     * Resets the player name to the original if modified
     *
     * @param player The player to reset
     * @param reloadPlayer If it should reload the player from all clients
     */
    @JvmStatic
    @JvmOverloads
    fun resetName(player: Player, reloadPlayer: Boolean = true) = PlayerMetaManager.trueGameProfile[player.uniqueId]?.let { setName(player, it.name, reloadPlayer) }

    /**
     * Resets the player skin to the original if modified
     *
     * @param player The player to reset
     * @param reloadPlayer If it should reload the player from all clients
     */
    @JvmStatic
    @JvmOverloads
    fun resetSkin(player: Player, reloadPlayer: Boolean = true) = PlayerMetaManager.trueGameProfile[player.uniqueId]?.let { setSkin(player, it.skin, reloadPlayer) }

    /**
     * Changes the player client cape
     *
     * TO MAKE IT CLEAR. THIS WILL ONLY DISPLAY ON THE CLIENT.
     *
     * @param player The player to change
     * @param cape The cape to change to
     */
    @JvmStatic
    fun setCape(player: Player, cape: Cape) {
        val event = PlayerCapeChangeEvent(player, getCape(player), cape)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) return
        val currentSkin = getPlayerSkin(player)
        val json = JsonParser.parseString(String(Base64.getDecoder().decode(currentSkin.texture))).asJsonObject
        val capeJson = json.get("textures").asJsonObject.get("CAPE").asJsonObject
        val modifiedJson = capeJson.apply { addProperty("url", cape.texture) }
        val modifiedTextureJson = json.get("textures").asJsonObject.apply { add("CAPE", modifiedJson) }
        val finalJson = json.apply { add("textures", modifiedTextureJson) }
        setSkin(player, Base64.getEncoder().encodeToString(finalJson.toString().toByteArray()), currentSkin.signature, false)
        reloadPlayerMeta(player)
    }

    /**
     * Get the player client cape
     *
     * @param player The player to get the cape from
     * @return The current cape
     */
    @JvmStatic
    fun getCape(player: Player): Cape = PlayerMetaManager.modifiedGameProfile[player.uniqueId]!!.let {
        val json = JsonParser.parseString(String(Base64.getDecoder().decode(it.skin.texture))).asJsonObject
        val texture = json.get("textures").asJsonObject
        if (!texture.has("CAPE")) return@let Cape.NONE
        val capeJson = texture.get("CAPE").asJsonObject
        return@let Cape.entries.firstOrNull { it.texture == capeJson.get("url").asString } ?: Cape.NONE
    }

    /**
     * Get the player original client cape
     *
     * @param player The player to get the cape from
     * @return The current cape
     */
    @JvmStatic
    fun getOriginalCape(player: Player): Cape = PlayerMetaManager.trueGameProfile[player.uniqueId]!!.let {
        val json = JsonParser.parseString(String(Base64.getDecoder().decode(it.skin.texture))).asJsonObject
        val capeJson = json.get("textures").asJsonObject.get("CAPE").asJsonObject
        return@let Cape.entries.firstOrNull { it.texture == capeJson.get("url").asString } ?: Cape.NONE
    }

    /**
     * The current players game profile
     *
     * @param player The player to get the game-profile from
     * @return The current game-profile
     */
    @JvmStatic
    fun getGameProfile(player: Player): GameProfile = PlayerMetaManager.modifiedGameProfile[player.uniqueId]!!

    /**
     * The original players game profile
     *
     * @param player The player to get the game-profile from
     * @return The original game-profile
     */
    @JvmStatic
    fun getOriginalGameProfile(player: Player): GameProfile = PlayerMetaManager.trueGameProfile[player.uniqueId]!!

    /**
     *  Check if the player has a modified game-profile
     *
     *  @param player That player to check
     */
    @JvmStatic
    fun hasModifiedGameProfile(player: Player): Boolean = PlayerMetaManager.trueGameProfile[player.uniqueId] == PlayerMetaManager.modifiedGameProfile[player.uniqueId]

    /**
     * Sets the player game-profile
     *
     * @param player The player to modify
     * @param gameProfile The gameProfile to change too
     * @param reloadPlayer If it should reload the player from all clients
     */
    @JvmStatic
    @JvmOverloads
    fun setGameProfile(player: Player, gameProfile: GameProfile, reloadPlayer: Boolean = true) {
        if (player.name != gameProfile.name) setName(player, gameProfile.name, reloadPlayer)
        if (player.getSkin() != gameProfile.skin) setSkin(player, gameProfile.skin, reloadPlayer)
    }
}

/**
 * Get the player original client cape
 *
 * @return The current cape
 */
fun Player.getOriginalCape(): Cape = PlayerMetaUtil.getOriginalCape(this)

/**
 * Get the player client cape
 *
 * @return The current cape
 */
fun Player.getCape(): Cape = PlayerMetaUtil.getCape(this)

/**
 * The current players game profile
 *
 * @return The current game-profile
 */
fun Player.getGameProfile(): GameProfile = PlayerMetaUtil.getGameProfile(this)

/**
 * The original players game profile
 *
 * @return The original game-profile
 */
fun Player.getOriginalGameProfile(): GameProfile = PlayerMetaUtil.getOriginalGameProfile(this)

/**
 *  Check if the player has a modified game-profile
 */
fun Player.hasModifiedGameProfile(): Boolean = PlayerMetaUtil.hasModifiedGameProfile(this)

/**
 * Sets the player game-profile
 *
 * @param gameProfile The gameProfile to change too
 * @param reloadPlayer If it should reload the player from all clients
 */
fun Player.setGameProfile(gameProfile: GameProfile, reloadPlayer: Boolean = true) = PlayerMetaUtil.setGameProfile(this, gameProfile, reloadPlayer)

/**
 * Changes the player client cape
 *
 * TO MAKE IT CLEAR. THIS WILL ONLY DISPLAY ON THE CLIENT.
 *
 * @param cape The cape to change to
 */
fun Player.setCape(cape: Cape) = PlayerMetaUtil.setCape(this, cape)

/**
 * Changes the player username
 *
 * @param name The new name
 * @param reloadPlayer If it should reload the player from all clients
 */
fun Player.setName(name: String, reloadPlayer: Boolean = true) = PlayerMetaUtil.setName(this, name, reloadPlayer)

/**
 * Changes the player skin
 *
 * @param skin The new skin
 * @param reloadPlayer If it should reload the player from all clients
 */
fun Player.setSkin(skin: Skin, reloadPlayer: Boolean = true) = setSkin(skin.texture, skin.signature, reloadPlayer)

/**
 * Changes the player skin
 *
 * @param texture The skin texture
 * @param signature The skin signature
 * @param reloadPlayer If it should reload the player from all clients
 */
fun Player.setSkin(texture: String, signature: String, reloadPlayer: Boolean = true) =
    PlayerMetaUtil.setSkin(this, texture, signature, reloadPlayer)

/**
 * Reload the player for the client. It is used to change that player skin for the client
 */
fun Player.reloadPlayerMeta() = PlayerMetaUtil.reloadPlayerMeta(this)

/**
 * Reload the player meta for all other clients.
 */
fun Player.reloadPlayerMetaGlobal() = PlayerMetaUtil.reloadPlayerMetaGlobal(this)

/**
 * Get the original name of the player
 *
 * @return The original player name
 */
fun Player.getOriginalName(): String = PlayerMetaUtil.getOriginalPlayerName(this)

/**
 * Get the current player skin
 *
 * @return The current player skin
 */
fun Player.getSkin(): Skin = PlayerMetaUtil.getPlayerSkin(this)

/**
 * Get the original player skin
 *
 * @return The original player skin
 */
fun Player.getOriginalSkin(): Skin = PlayerMetaUtil.getOriginalPlayerSkin(this)

/**
 * Check if the player name is the original
 */
fun Player.isNameModified(): Boolean = PlayerMetaUtil.isNameModified(this)

/**
 * Check if the player skin is the original
 */
fun Player.isSkinModified(): Boolean = PlayerMetaUtil.isSkinModified(this)

/**
 * Resets the player name to the original if modified
 *
 * @param reloadPlayer If it should reload the player from all clients
 */
fun Player.resetName(reloadPlayer: Boolean = true) = PlayerMetaUtil.resetName(this, reloadPlayer)

/**
 * Resets the player skin to the original if modified
 *
 * @param reloadPlayer If it should reload the player from all clients
 */
fun Player.resetSkin(reloadPlayer: Boolean = true) = PlayerMetaUtil.resetSkin(this, reloadPlayer)