package com.undefined.lynx.sidebar.sidebar.interfaces

interface TimerLine<T>: Updatable<T> {
    fun stop(): T
    fun start(ticks: Int, async: Boolean): T
    fun start(ticks: Int): T = start(ticks, false)
}