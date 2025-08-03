package com.undefined.lynx.scheduler

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.util.RunBlock
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.math.floor

/**
 * This class managers all the scheduler methods
 */
object Scheduler {
    /**
     * Will run code sync
     */
    @JvmStatic
    fun sync(runnable: Consumer<BukkitRunnable>) = createRunnable(runnable).runTask(LynxConfig.javaPlugin)
    /**
     * Will run code async
     */
    @JvmStatic
    fun async(runnable: Consumer<BukkitRunnable>) = createRunnable(runnable).runTaskAsynchronously(LynxConfig.javaPlugin)
    /**
     * Will run code with a delay using ticks
     *
     * @param ticks The number of ticks to wait
     * @param unit If not, null will use to convert the ticks into the new time
     * @param async If to run the code async
     */
    @JvmStatic
    @JvmOverloads
    fun delay(ticks: Int, unit: TimeUnit? = null, async: Boolean = false, runnable: Consumer<BukkitRunnable>) =
        if (async) createRunnable(runnable).runTaskLaterAsynchronously(LynxConfig.javaPlugin, unit.toTicks(ticks)) else createRunnable(runnable).runTaskLater(LynxConfig.javaPlugin, unit.toTicks(ticks))

    /**
     * Will run code repeating
     *
     * @param ticks The ticks between the tasks
     * @param period The ticks before the loop will start
     * @param unit If not, null will use to convert the ticks into the new time
     * @param times If higher than -1 it's the number of times it will run
     * @param async If it should run the code async
     */
    @JvmStatic
    @JvmOverloads
    fun repeatingTask(ticks: Int, period: Int = 0, unit: TimeUnit? = null, async: Boolean = false, times: Int = -1, runnable: Consumer<BukkitRunnable>) =
        createRunnable(times, runnable).let {
            if (async) {
                it.runTaskTimerAsynchronously(LynxConfig.javaPlugin, unit.toTicks(ticks), unit.toTicks(period))
            } else {
                it.runTaskTimer(LynxConfig.javaPlugin, unit.toTicks(ticks), unit.toTicks(period))
            }
        }

    /**
     * Will run code repeating until supplier returns false
     *
     * @param ticks The ticks between the tasks
     * @param period The ticks before the loop will start
     * @param unit If not, null will use to convert the ticks into the new time
     * @param async If it should run the code async
     * @param condition The condition of the supplier returns false it will stop the loop
     */
    @JvmStatic
    @JvmOverloads
    fun repeatingTask(ticks: Int, period: Int = 0, unit: TimeUnit? = null, async: Boolean = false, condition: Supplier<Boolean>, runnable: Consumer<BukkitRunnable>) =
        createRunnable(condition, runnable).let {
            if (async) {
                it.runTaskTimerAsynchronously(LynxConfig.javaPlugin, unit.toTicks(ticks), unit.toTicks(period))
            } else {
                it.runTaskTimer(LynxConfig.javaPlugin, unit.toTicks(ticks), unit.toTicks(period))
            }
        }

}

/**
 * Will run code sync
 */
fun sync(runnable: (BukkitRunnable) -> Unit) = Scheduler.sync(runnable)

/**
 * Will run code async
 */
fun async(runnable: (BukkitRunnable) -> Unit) = Scheduler.async(runnable)

/**
 * Will run code with a delay using ticks
 *
 * @param ticks The number of ticks to wait
 * @param unit If not, null will use to convert the ticks into the new time
 * @param async If to run the code async
 */
fun delay(ticks: Int, unit: TimeUnit? = null, async: Boolean = false, runnable: (BukkitRunnable) -> Unit) = Scheduler.delay(ticks, unit, async, runnable)

/**
 * Will run code repeating
 *
 * @param ticks The ticks between the tasks
 * @param period The ticks before the loop will start
 * @param unit If not, null will use to convert the ticks into the new time
 * @param times If higher than -1 it's the number of times it will run
 * @param async If it should run the code async
 */
fun repeatingTask(ticks: Int, period: Int = 0, times: Int = -1, unit: TimeUnit? = null, async: Boolean = false, runnable: (BukkitRunnable) -> Unit): BukkitTask =
    Scheduler.repeatingTask(ticks, period, unit, async, times, runnable)

/**
 * Will run code repeating until supplier returns false
 *
 * @param ticks The ticks between the tasks
 * @param period The ticks before the loop will start
 * @param unit If not, null will use to convert the ticks into the new time
 * @param async If it should run the code async
 * @param condition The condition of the supplier returns false it will stop the loop
 */
fun repeatingTask(ticks: Int, period: Int = 0, unit: TimeUnit? = null, async: Boolean = false, condition: Unit.() -> Boolean, runnable: (BukkitRunnable) -> Unit): BukkitTask =
    Scheduler.repeatingTask(ticks, period, unit, async, { condition.invoke(Unit) }, runnable)

private fun TimeUnit?.toTicks(amount: Int): Long {
    if (this == null) return amount.toLong()
    return floor((this.convert(amount.toLong(), TimeUnit.SECONDS) * 20.0)).toLong()
}

private fun createRunnable(runnable: Consumer<BukkitRunnable>): BukkitRunnable {
    return object : BukkitRunnable() {
        override fun run() {
            runnable.accept(this)
        }
    }
}

private fun createRunnable(condition: Supplier<Boolean>, runnable: Consumer<BukkitRunnable>): BukkitRunnable =
    createRunnable {
        if (!condition.get()) {
            it.cancel()
            return@createRunnable
        }
        runnable.accept(it)
    }

private fun createRunnable(times: Int, runnable: Consumer<BukkitRunnable>): BukkitRunnable {
    if (times <= -1) return createRunnable(runnable)
    var amount = 0
    return createRunnable {
        if (times == amount) {
            it.cancel()
            return@createRunnable
        }
        amount++
        runnable.accept(it)
    }
}