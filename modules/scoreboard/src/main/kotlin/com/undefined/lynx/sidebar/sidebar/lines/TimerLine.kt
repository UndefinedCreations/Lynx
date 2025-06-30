package com.undefined.lynx.sidebar.sidebar.lines

import org.bukkit.scheduler.BukkitTask

open class TimerLine(
    sideBarTeam: SideBarTeam,
    order: String,
    var bukkitTask: BukkitTask?
): TeamLine("", sideBarTeam, order)