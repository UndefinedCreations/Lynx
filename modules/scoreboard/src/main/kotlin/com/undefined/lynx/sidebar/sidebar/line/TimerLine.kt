package com.undefined.lynx.sidebar.sidebar.line

import com.undefined.lynx.sidebar.toJson
import net.kyori.adventure.text.Component
import org.bukkit.scheduler.BukkitTask

open class TimerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var task: BukkitTask?
) : TeamLine("".toJson(), sideBarTeam, order)