package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.itembuilder.ItemBuilder
import com.undefined.lynx.itembuilder.SkullMeta
import com.undefined.lynx.logger.sendInfo
import com.undefined.lynx.nick.getOriginalName
import com.undefined.lynx.nick.resetName
import com.undefined.lynx.nick.resetSkin
import com.undefined.lynx.nick.setName
import com.undefined.lynx.nick.setSkin
import com.undefined.lynx.not
import com.undefined.lynx.npc.spawnNPC
import com.undefined.lynx.plus
import com.undefined.lynx.scheduler.repeatingTask
import com.undefined.lynx.sidebar.sidebar.lines.BasicLine
import com.undefined.lynx.sidebar.sidebar.lines.Line
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.tab.layout.tabLayout
import com.undefined.lynx.tab.size.fillTab
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.time.Instant
import java.time.format.DateTimeFormatter

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        nick()
        npc()
        scoreboared()
        tab()
        customTab()
    }

    fun customTab() {
        StellarCommand("tab")
            .addExecution<Player> {
                tabLayout(async = false) {
                    setPlayer(30, sender)
                    setLatency(40, TabLatency.BAR_3)
                    setPerPlayerStringTextLine(0) { "${ChatColor.AQUA} Ping : ${ChatColor.GRAY}${ping}" }
                }.addPlayer(sender)
            }.register()
    }

    fun tab() {
        StellarCommand("fillTab")
            .addExecution<Player> {
                sender.fillTab()
            }.register()
    }

    fun scoreboared() {
        val mainLevel = StellarCommand("scoreboard")

        mainLevel.addExecution<Player> {

            val sideBar = sidebar("Survival") {
                addEmptyLine()
                addUpdatablePlayerLine { "${ChatColor.AQUA}${it.name}" }
                addUpdatablePlayerTimerLine(20) { "${ChatColor.RED}Kills : ${ChatColor.GRAY}${it.getStatistic(Statistic.PLAYER_KILLS)}" }
                addUpdatablePlayerTimerLine(20) { "${ChatColor.DARK_PURPLE}Kills : ${ChatColor.GRAY}${it.getStatistic(Statistic.DEATHS)}" }
                addUpdatablePlayerTimerLine(20) { "${ChatColor.DARK_AQUA}Ping : ${ChatColor.GRAY}${it.ping}" }
                addEmptyLine()
                addUpdatablePlayerTimerLine(20) { "${ChatColor.AQUA}Rank : ${ChatColor.GRAY}OWNER" }
                addUpdatableTimerLine(20) { "${ChatColor.AQUA}Online : ${ChatColor.GRAY}${Bukkit.getOnlinePlayers().size}" }
                addEmptyLine()
                addLine(Line("LYNX :D"))
            }

            sideBar.addViewer(sender)

        }

        mainLevel.register()
    }

    fun npc() {
        val mainLevel = StellarCommand("npc")

        mainLevel.addExecution<Player> {
            val npc = sender.location.spawnNPC("Testing")

            var pastLoc: Location? = null

            Bukkit.getScheduler().runTaskTimer(this@Main, Runnable {
                if (pastLoc == null) {
                    pastLoc = sender.location
                    return@Runnable
                }
                npc.lookAt(pastLoc!!)
                pastLoc = sender.location
            }, 1, 1)

        }

        mainLevel.register()
    }

    fun nick() {
        val mainLevel = StellarCommand("disguise");

        mainLevel.addArgument("name")
            .addStringArgument("newName")
            .addExecution<Player> {
                val newName: String by args
                if (newName.length > 16) {
                    sender.sendMessage("${ChatColor.RED}The passed username isn't a valid name")
                    return@addExecution
                }
                sender.setName(newName)
            }

        mainLevel.addArgument("skin")
            .addStringArgument("texture")
            .addStringArgument("signature")
            .addExecution<Player> {
                val texture: String by args
                val signature: String by args
                sender.setSkin(texture, signature)
            }

        mainLevel.addArgument("reset")
            .addExecution<Player> {
                sender.getOriginalName().sendInfo()
                sender.resetName()
                sender.resetSkin()
            }

        mainLevel.register();
    }

}
