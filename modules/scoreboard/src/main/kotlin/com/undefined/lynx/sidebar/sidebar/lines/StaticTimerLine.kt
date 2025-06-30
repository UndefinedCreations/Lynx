package com.undefined.lynx.sidebar.sidebar.lines

import org.bukkit.scheduler.BukkitTask

class StaticTimerLine(
    sideBarTeam: SideBarTeam,
    order: String,
    var run: Unit.() -> String,
    bukkitTask: BukkitTask?
): TimerLine(sideBarTeam, order, bukkitTask)