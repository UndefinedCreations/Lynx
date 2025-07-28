package com.undefined.lynx.adventure

import com.google.gson.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer


private val gson: Gson? by lazy {
    try {
        ComponentSerializer::class.java.getDeclaredField("gson").apply { isAccessible = true }.get(null) as Gson
    } catch (e: Exception) {
        null
    }
}

fun Component.toJson(): String = JSONComponentSerializer.json().serialize(this)
fun String.toJson(): String = ComponentSerializer.toString(TextComponent(this)).toString()

fun Component.toLegacyText(): String = if (gson == null) ComponentSerializer.deserialize(this.toJson()).toLegacyText() else JsonParser.parseString(this.toJson()).deserialize(gson!!).toLegacyText()

fun JsonElement.deserialize(gson: Gson): BaseComponent {
    if (this is JsonPrimitive) {
        if (this.isString) {
            return TextComponent(this.asString)
        }
    } else if (this is JsonArray) {
        val array = gson.fromJson(this, Array<BaseComponent>::class.java) as Array<BaseComponent>
        return TextComponent(*array)
    }
    return gson.fromJson(this, BaseComponent::class.java)
}