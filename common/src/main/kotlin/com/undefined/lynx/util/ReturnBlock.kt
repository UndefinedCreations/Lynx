package com.undefined.lynx.util

fun interface ReturnBlock<T, U> {
    fun run(block: T): U
}