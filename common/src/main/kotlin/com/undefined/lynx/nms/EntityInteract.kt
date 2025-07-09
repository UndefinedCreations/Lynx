package com.undefined.lynx.nms

import org.bukkit.entity.Player

data class EntityInteract(
    val entityID: Int,
    val clickType: ClickType,
    val player: Player
)

enum class ClickType {
    RIGHT,
    LEFT
}