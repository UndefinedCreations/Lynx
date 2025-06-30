package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.sidebar.team.team
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {

        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        val bar = sidebar("test", async = true) {

            addDynamicPlayerTimerLine("tests", 1) { "${ChatColor.AQUA}Ping: ${ChatColor.GRAY}${this.inventory.getItem(
                EquipmentSlot.HAND)?.type?.name}" }

        }

        val team = team(autoLoad = false) {
            prefix = "Testing UwU "
            color = ChatColor.RED
        }


        StellarCommand("test")
            .addExecution<Player> {

                bar.addViewer(sender)
                team.addViewer(sender)
                team.addEntry(sender)

            }.register()

    }

    override fun onDisable() {



    }

}

enum class Type() {
    RANK
}