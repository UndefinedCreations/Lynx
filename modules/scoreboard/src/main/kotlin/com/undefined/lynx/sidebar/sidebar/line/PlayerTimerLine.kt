package com.undefined.lynx.sidebar.sidebar.line

import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class PlayerTimerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var run: Player.() -> String,
    bukkitTask: BukkitTask?
) : TimerLine(sideBarTeam, order, bukkitTask)