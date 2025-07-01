package com.undefined.lynx.sidebar.sidebar.line

import org.bukkit.scheduler.BukkitTask

open class TimerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var task: BukkitTask?
): TeamLine("", sideBarTeam, order)