package com.undefined.lynx.event

import com.undefined.lynx.LynxConfig
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

inline fun <reified T : Event> event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline callback: T.() -> Unit
): Listener = LynxListener().apply {
    LynxConfig.javaPlugin.server.pluginManager.registerEvent(
        T::class.java,
        this,
        priority,
        { _, event ->
            if (event is T) callback(event)
        },
        LynxConfig.javaPlugin,
        ignoreCancelled
    )
}

class LynxListener : Listener {
    fun unregister() = HandlerList.unregisterAll(this)
}