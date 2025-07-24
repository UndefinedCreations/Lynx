package com.undefined.lynx.nick.events

import com.undefined.lynx.nick.Cape
import org.bukkit.entity.Player

/**
 * Event will run when the player cape is changed
 *
 * @param player The player that is being modified
 * @param oldCape The old cape
 * @param newCape The new cape
 */
class PlayerCapeChangeEvent(
    player: Player,
    val oldCape: Cape,
    val newCape: Cape
) : PlayerGameProfileChangeEvent(player)