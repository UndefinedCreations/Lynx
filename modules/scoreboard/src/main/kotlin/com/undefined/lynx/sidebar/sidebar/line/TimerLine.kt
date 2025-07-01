package com.undefined.lynx.sidebar.sidebar.line

import net.kyori.adventure.text.Component
import org.bukkit.scheduler.BukkitTask

open class TimerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var task: BukkitTask?
) : TeamLine(Component.empty(), sideBarTeam, order)