package com.undefined.lynx.event

import org.bukkit.event.Cancellable

class CancellableLynxEvent @JvmOverloads constructor(async: Boolean = false): LynxEvent(async), Cancellable {

    private var cancelled = false

    override fun isCancelled() = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

}