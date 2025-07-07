package com.undefined.lynx.event

import org.bukkit.Bukkit
import org.bukkit.event.Event

object LynxEventUtil {

    fun call(event: Event) = Bukkit.getPluginManager().callEvent(event)

}

fun Event.call() = LynxEventUtil.call(this)