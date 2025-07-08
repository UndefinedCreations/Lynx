package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

internal inline fun <T> T.checkAsyncAndApply(async: Boolean, crossinline block: T.() -> Unit): T = apply {
    if (async) CompletableFuture.supplyAsync { Runnable { block() } } else block()
}

internal fun order(index: Int): String = (index.toChar().code + 1).toChar().toString()