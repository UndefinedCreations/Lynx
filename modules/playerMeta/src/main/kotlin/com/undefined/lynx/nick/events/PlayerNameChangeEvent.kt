package com.undefined.lynx.nick.events

import org.bukkit.entity.Player

/**
 * Event will run when the player name is changed
 *
 * @param player The player that is being modified
 * @param oldName The old name
 * @param newName The new name
 */
class PlayerNameChangeEvent(
    player: Player,
    val oldName: String,
    val newName: String
) : PlayerGameProfileChangeEvent(player)