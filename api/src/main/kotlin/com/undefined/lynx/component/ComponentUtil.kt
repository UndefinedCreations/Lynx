package com.undefined.lynx.component

import com.undefined.lynx.LynxConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

fun Component.legacyString(): String = LegacyComponentSerializer.legacyAmpersand().serialize(this)
fun String.miniMessage(): Component = LynxConfig.miniMessage.deserialize(this)
fun String.component(): Component = Component.text(this)