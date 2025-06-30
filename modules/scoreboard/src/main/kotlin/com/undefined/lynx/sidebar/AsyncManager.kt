package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import org.bukkit.Bukkit
import org.bukkit.ChatColor

internal inline fun <T> T.checkAsyncAndApply(async: Boolean, crossinline block: T.() -> Unit): T = apply {
    if (async) Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable { block() }) else block()
}

internal fun order(time: Int): String {
    return "§" + ('a'.code + time).toChar().toString() + ChatColor.RESET
}