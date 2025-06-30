package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.scheduler.delay
import com.undefined.lynx.scheduler.repeatingTask
import com.undefined.lynx.sidebar.sidebar.SideBar
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import com.undefined.stellar.util.unregisterCommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Main : JavaPlugin() {

    override fun onEnable() {

        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        StellarCommand("test")
            .addExecution<Player> {

                sidebar("test") {

                    addViewer(sender)
                    addDynamicPlayerLine("Test") { player!!.name }

                }

            }.register()

    }

    override fun onDisable() {



    }

}

enum class Type() {
    RANK
}