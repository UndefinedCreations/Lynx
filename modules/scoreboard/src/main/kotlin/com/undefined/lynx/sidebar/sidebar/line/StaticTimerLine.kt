package com.undefined.lynx.sidebar.sidebar.line

import net.kyori.adventure.text.Component
import org.bukkit.scheduler.BukkitTask

class StaticTimerLine(
    sideBarTeam: SidebarTeam,
    order: String,
    var run: () -> Component,
    bukkitTask: BukkitTask?
) : TimerLine(sideBarTeam, order, bukkitTask)