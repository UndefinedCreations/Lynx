package com.undefined.lynx.nms

import org.bukkit.entity.Player

data class NPCInteract(
    val entityID: Int,
    val clickType: ClickType,
    val player: Player
)

enum class ClickType {
    RIGHT,
    LEFT
}