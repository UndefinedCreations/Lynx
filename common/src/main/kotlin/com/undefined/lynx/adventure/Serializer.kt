package com.undefined.lynx.adventure

import com.google.gson.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer


private val gson: Gson = ComponentSerializer::class.java.getDeclaredField("gson").apply { isAccessible = true }.get(null) as Gson

fun Component.toJson(): String = JSONComponentSerializer.json().serialize(this)
fun String.toJson(): String = ComponentSerializer.toString(TextComponent(this)).toString()

fun Component.toLegacyText(): String = JsonParser.parseString(this.toJson()).deserialize().toLegacyText()

fun JsonElement.deserialize(): BaseComponent {
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