package com.undefined.lynx

data class Skin(
    var texture: String,
    var signature: String,
) {
    override fun equals(other: Any?): Boolean {
        other ?: return super.equals(other)
        if (other is Skin) return other.texture == texture && other.signature == signature
        return super.equals(other)
    }
}