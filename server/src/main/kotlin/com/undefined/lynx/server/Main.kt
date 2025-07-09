package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.display.implementions.Interaction
import com.undefined.lynx.display.implementions.TextDisplay
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.util.miniMessage
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        StellarCommand("test")
            .addExecution<Player> {
                sidebar("Testing") {
                    addDynamicPlayerLine("test") { "<red>test".miniMessage() }
                    addViewer(sender)
                }

            }.register()

    }

}
