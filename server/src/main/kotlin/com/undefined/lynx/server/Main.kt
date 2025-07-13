package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.lines.UpdatablePlayerLine
import com.undefined.lynx.sidebar.sidebar.lines.UpdatablePlayerTimerLine
import com.undefined.lynx.sidebar.sidebar.lines.UpdatableTimerLine
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.util.toMiniMessageOrDefault
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)
        LynxConfig.setMiniMessage(MiniMessage.miniMessage())

        val line = UpdatablePlayerTimerLine(20) { "<aqua>Block <gray>: ${it.inventory.itemInMainHand.type.name}" }
        val side = sidebar("Testing") {
            addLine(line)
        }

        StellarCommand("test")
            .addExecution<Player> {
                side.addViewers(listOf(sender))

            }.register()

    }

}
