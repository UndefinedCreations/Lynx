package com.undefined.lynx.nick.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList


/**
 * Event will run when the player GameProfile is changed
 *
 * @param player The player that is being modified
 */
open class PlayerGameProfileChangeEvent(
    val player: Player
) : Event(), Cancellable {

    private var cancelled = false

    override fun isCancelled() = cancelled

    /**
     * Option to cancel game-profile changes
     */
    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    companion object {
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}