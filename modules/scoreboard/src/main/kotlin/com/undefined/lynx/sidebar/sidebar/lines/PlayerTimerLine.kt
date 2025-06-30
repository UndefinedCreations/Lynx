package com.undefined.lynx.sidebar.sidebar.lines

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class PlayerTimerLine(
    sideBarTeam: SideBarTeam,
    order: String,
    var run: Player.() -> String,
    bukkitTask: BukkitTask?
): TimerLine(sideBarTeam, order, bukkitTask){
}