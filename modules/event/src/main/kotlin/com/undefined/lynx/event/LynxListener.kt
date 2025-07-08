package com.undefined.lynx.event

import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class LynxListener : Listener {
    fun unregister() = HandlerList.unregisterAll(this)
}