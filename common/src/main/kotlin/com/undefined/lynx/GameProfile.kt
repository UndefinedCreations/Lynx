package com.undefined.lynx

data class GameProfile(
    var name: String,
    var skin: Skin
) {

    fun clone(): GameProfile = GameProfile(name, skin)

    override fun equals(other: Any?): Boolean {
        other ?: return super.equals(other)
        if (other is GameProfile) return other.name == name && other.skin == skin
        return super.equals(other)
    }
}