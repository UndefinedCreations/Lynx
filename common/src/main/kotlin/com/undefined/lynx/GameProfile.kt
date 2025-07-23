package com.undefined.lynx

data class GameProfile(
    val name: String,
    val skin: Skin
) {

    override fun equals(other: Any?): Boolean {
        other ?: return super.equals(other)
        if (other is GameProfile) return other.name == name && other.skin == skin
        return super.equals(other)
    }
}