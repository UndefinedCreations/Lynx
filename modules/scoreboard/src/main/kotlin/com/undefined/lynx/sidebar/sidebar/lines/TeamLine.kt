package com.undefined.lynx.sidebar.sidebar.lines

import org.bukkit.ChatColor

open class TeamLine(
    text: String,
    val sideBarTeam: SideBarTeam,
    order: String,
) : Line(text, order) {
}

data class SideBarTeam(
    val team: Any,
    val id: Any
)