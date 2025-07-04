package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.sidebar.team.team
import com.undefined.lynx.util.miniMessage
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)



        val team = team {
            color = ChatColor.RED

        }

        StellarCommand("test")
            .addExecution<Player> {

                val bar = sidebar("<red>test") {
                    addViewer(sender)
                    addLine("${ChatColor.AQUA}1")
                    addEmptyLine()
                    addDynamicLine("Test", "<white>Testing <aqua>idk <red>asdas".miniMessage())

                    modifyDynamicLine("Test", "<white>Testing <aqua>idk <white>asdas".miniMessage())

                }


                team.addEntry(sender)
            }.register()
    }

}

operator fun String.not(): Component = MiniMessage.miniMessage().deserialize(this)