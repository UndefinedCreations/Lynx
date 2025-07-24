package com.undefined.lynx.nick.events

import com.undefined.lynx.Skin
import org.bukkit.entity.Player

/**
 * Event will run when the player skin is changed
 *
 * @param player The player that is being modified
 * @param oldSkin The old skin
 * @param newSkin The new skin
 */
class PlayerSkinChangeEvent(
    player: Player,
    val oldSkin: Skin,
    val newSkin: Skin
) : PlayerGameProfileChangeEvent(player)