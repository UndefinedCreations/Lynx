package com.undefined.lynx.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

fun Component.legacyString(): String = LegacyComponentSerializer.legacySection().serialize(this)
fun String.miniMessage(): Component = MiniMessage.miniMessage().deserialize(this)
fun String.component(): Component = Component.text(this)