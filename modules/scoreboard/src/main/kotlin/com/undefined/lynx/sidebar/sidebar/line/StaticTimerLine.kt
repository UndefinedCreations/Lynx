package com.undefined.lynx.sidebar.sidebar.line

import org.bukkit.scheduler.BukkitTask

class StaticTimerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var run: Unit.() -> String,
    bukkitTask: BukkitTask?
): TimerLine(sideBarTeam, order, bukkitTask)