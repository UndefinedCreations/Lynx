package com.undefined.lynx.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer

fun Component.toJson(): String = JSONComponentSerializer.json().serialize(this)
fun String.toJson(): String = ComponentSerializer.toJson(TextComponent(this)).toString()

fun Component.toLegacyText(): String = ComponentSerializer.deserialize(this.toJson()).toLegacyText()