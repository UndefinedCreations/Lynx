package com.undefined.lynx.util

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.adventure.toLegacyText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun String.miniMessage(): Component = MiniMessage.miniMessage().deserialize(this)
fun String.component(): Component = Component.text(this)

fun String.toMiniMessageOrDefault(): String = if (LynxConfig.miniMessage == null) this else this.miniMessage().toLegacyText()

object ComponentUtil {
    @JvmStatic
    fun stringToComponent(string: String) = Component.text(string)
    @JvmStatic
    fun stringToMiniMessage(string: String) = MiniMessage.miniMessage().deserialize(string)
}