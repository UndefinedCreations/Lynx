package com.undefined.lynx.scheduler

import org.bukkit.scheduler.BukkitRunnable

interface SchedulerBlock {
    fun run(task: BukkitRunnable)
}