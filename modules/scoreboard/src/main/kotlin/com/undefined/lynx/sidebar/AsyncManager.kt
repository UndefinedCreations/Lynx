package com.undefined.lynx.sidebar

import com.undefined.lynx.LynxConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor

internal inline fun <T> T.checkAsyncAndApply(async: Boolean, crossinline block: T.() -> Unit): T = apply {
    if (async) Bukkit.getScheduler().runTaskAsynchronously(LynxConfig.javaPlugin, Runnable { block() }) else block()
}

internal fun order(index: Int): String = (index.toChar().code + 1).toChar().toString()

internal fun Component.toJson(): String = JSONComponentSerializer.json().serialize(this)
internal fun String.toJson(): String = ComponentSerializer.toJson(TextComponent(this)).toString()
