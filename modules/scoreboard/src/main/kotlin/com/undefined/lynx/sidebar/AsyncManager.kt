package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import org.bukkit.Bukkit

inline fun <T> T.checkAsyncAndApply(async: Boolean, crossinline block: T.() -> Unit): T = apply {
    if (async) Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable { block() }) else block()
}