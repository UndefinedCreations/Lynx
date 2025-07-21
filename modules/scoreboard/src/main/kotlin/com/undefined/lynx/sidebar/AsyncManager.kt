package com.undefined.lynx.sidebar

import java.util.concurrent.CompletableFuture

internal inline fun <T> T.checkAsyncAndApply(async: Boolean, crossinline block: T.() -> Unit): T = apply {
    if (async) CompletableFuture.supplyAsync { Runnable { block() } } else block()
}

internal fun order(index: Int): String = (index.toChar().code + 1).toChar().toString()