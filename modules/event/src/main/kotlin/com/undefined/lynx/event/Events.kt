package com.undefined.lynx.event

import com.undefined.lynx.LynxConfig
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

inline fun <reified T : Event> event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: (T) -> Unit,
): Listener = LynxListener().also { listener ->
    LynxConfig.javaPlugin.server.pluginManager.registerEvent(
        T::class.java,
        listener,
        priority,
        { _, event ->
            if (event is T) block(event)
        },
        LynxConfig.javaPlugin,
        ignoreCancelled
    )
}

object Events {
    @JvmStatic
    @JvmOverloads
    fun <T : Event> event(
        eventClass: Class<T>,
        block: EventBlock<T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
    ): Listener = LynxListener().apply {
        LynxConfig.javaPlugin.server.pluginManager.registerEvent(
            eventClass,
            this,
            priority,
            { _, event ->
                if (eventClass.isInstance(event)) block
            },
            LynxConfig.javaPlugin,
            ignoreCancelled,
        )
    }
}