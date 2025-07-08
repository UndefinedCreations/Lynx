package com.undefined.lynx.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun String.miniMessage(): Component = MiniMessage.miniMessage().deserialize(this)
fun String.component(): Component = Component.text(this)

object ComponentUtil {
    @JvmStatic
    fun stringToComponent(string: String) = Component.text(string)
    @JvmStatic
    fun stringToMiniMessage(string: String) = MiniMessage.miniMessage().deserialize(string)
}