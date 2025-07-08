package com.undefined.lynx.sidebar.sidebar.line

open class TeamLine(
    text: String,
    val sideBarTeam: SidebarTeam,
    order: String,
) : Line(text, order)

data class SidebarTeam(
    val team: Any,
    val id: Any
)