package com.undefined.lynx.event

import org.bukkit.event.Event

interface EventBlock<T : Event> {
    fun run(event: T)
}