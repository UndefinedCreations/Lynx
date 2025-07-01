package com.undefined.lynx.sidebar.sidebar.line

import net.kyori.adventure.text.Component

open class TeamLine(
    text: Component,
    val sideBarTeam: SidebarTeam,
    order: String,
) : Line(text, order)

data class SidebarTeam(
    val team: Any,
    val id: Any
)