package com.undefined.lynx.scheduler

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.util.RunBlock
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit
import kotlin.math.floor


object Scheduler {
    @JvmStatic
    fun sync(runnable: RunBlock<BukkitRunnable>) = sync { runnable.run(it) }
    @JvmStatic
    fun async(runnable: RunBlock<BukkitRunnable>) = async { runnable.run(it) }
    @JvmStatic
    @JvmOverloads
    fun delay(ticks: Int, unit: TimeUnit? = null, async: Boolean = false, runnable: RunBlock<BukkitRunnable>) = delay(ticks, unit, async) { runnable.run(it) }
    @JvmStatic
    @JvmOverloads
    fun repeatingTask(ticks: Int, period: Int = ticks, times: Int = -1, unit: TimeUnit? = null, async: Boolean = false, runnable: RunBlock<BukkitRunnable>) =
        repeatingTask(ticks, period, times, unit, async) { runnable.run(it) }
}

fun sync(runnable: (BukkitRunnable) -> Unit) = createRunnable(runnable).runTask(LynxConfig.javaPlugin)
fun async(runnable: (BukkitRunnable) -> Unit) = createRunnable(runnable).runTaskAsynchronously(LynxConfig.javaPlugin)

fun delay(ticks: Int, unit: TimeUnit? = null, async: Boolean = false, runnable: (BukkitRunnable) -> Unit) =
    if (async) {
        createRunnable(runnable).runTaskLaterAsynchronously(LynxConfig.javaPlugin, unit.toTicks(ticks.toLong()))
    } else {
        createRunnable(runnable).runTaskLater(LynxConfig.javaPlugin, unit.toTicks(ticks.toLong()))
    }

fun delay(ticks: Int = 1, runnable: (BukkitRunnable) -> Unit): BukkitTask =
    delay(ticks, false, runnable)

fun delay(ticks: Int = 1, async: Boolean, runnable: (BukkitRunnable) -> Unit): BukkitTask =
    delay(ticks, null, async, runnable)

fun repeatingTask(ticks: Int, period: Int = ticks, times: Int = -1, unit: TimeUnit? = null, async: Boolean = false, runnable: (BukkitRunnable) -> Unit): BukkitTask {
    return if (async) {
        createRunnable(times, runnable).runTaskTimerAsynchronously(LynxConfig.javaPlugin, unit.toTicks(ticks.toLong()), unit.toTicks(period.toLong()))
    } else {
        createRunnable(times, runnable).runTaskTimer(LynxConfig.javaPlugin, unit.toTicks(ticks.toLong()), unit.toTicks(period.toLong()))
    }
}

fun repeatingTask(ticks: Int = 1, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(0, ticks, -1, false, runnable)

fun repeatingTask(ticks: Int = 1, times: Int = -1, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(
        0,
        ticks,
        times,
        false,
        runnable
    )

fun repeatingTask(periodTicks: Int = 1, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(
        periodTicks,
        periodTicks,
        -1,
        async,
        runnable
    )

fun repeatingTask(periodTicks: Int = 1, async: Boolean, times: Int = -1, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(
        periodTicks,
        periodTicks,
        times,
        async,
        runnable
    )

fun repeatingTask(period: Int, unit: TimeUnit, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(period, period, -1, unit, false, runnable)


fun repeatingTask(period: Int, unit: TimeUnit, times: Int = -1, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(period, period, times, unit, false, runnable)

fun repeatingTask(period: Int, unit: TimeUnit, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(period, period, -1, unit, async, runnable)

fun repeatingTask(period: Int, unit: TimeUnit, times: Int = -1, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(period, period, times, unit, async, runnable)

fun repeatingTask(delayTicks: Int, periodTicks: Int, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(
        delayTicks,
        periodTicks,
        -1,
        async,
        runnable
    )

fun repeatingTask(delayTicks: Int, periodTicks: Int, times: Int = -1, async: Boolean, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(
        delayTicks,
        periodTicks,
        times,
        async,
        runnable
    )

fun repeatingTask(delay: Int, period: Int, unit: TimeUnit, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(delay, period, -1, unit, false, runnable)


fun repeatingTask(delay: Int, period: Int, times: Int = -1, unit: TimeUnit, runnable: BukkitRunnable.() -> Unit): BukkitTask =
    repeatingTask(delay, period, times, unit, false, runnable)


private fun TimeUnit?.toTicks(amount: Long): Long {
    if (this == null) return amount
    return floor((this.convert(amount, TimeUnit.SECONDS) * 20.0)).toLong()
}

private fun createRunnable(runnable: BukkitRunnable.() -> Unit): BukkitRunnable {
    return object : BukkitRunnable() {
        override fun run() {
            runnable()
        }
    }
}


private fun createRunnable(times: Int = -1, runnable: BukkitRunnable.() -> Unit): BukkitRunnable {
    var amount = 0
    return object : BukkitRunnable() {
        override fun run() {
            runnable()
            if (times == -1) return
            amount++
            if (amount >= times) cancel()
        }
    }
}