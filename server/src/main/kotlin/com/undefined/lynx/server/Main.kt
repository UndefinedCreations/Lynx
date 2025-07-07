package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.sidebar.team.team
import com.undefined.lynx.tab.size.fillTab
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


       

        StellarCommand("test")
            .addExecution<Player> {

            }.register()
    }

}
