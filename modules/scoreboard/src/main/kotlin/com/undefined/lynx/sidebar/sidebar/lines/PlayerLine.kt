package com.undefined.lynx.sidebar.sidebar.lines

import org.bukkit.entity.Player

class PlayerLine(
    sideBarTeam: SideBarTeam,
    order: String,
    var run: Player.() -> String
): TeamLine("", sideBarTeam, order){
}