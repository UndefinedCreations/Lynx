package com.undefined.lynx.sidebar.sidebar.line

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PlayerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var run: Player.() -> String
) : TeamLine("", sideBarTeam, order)