package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.itembuilder.ItemBuilder
import com.undefined.lynx.util.legacySectionString
import com.undefined.lynx.util.miniMessage
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)


       

        StellarCommand("test")
            .addExecution<Player> {

                val itemBuilder = ItemBuilder(Material.DIAMOND_HELMET)
                    .setName("<yellow>Woo: <rainbow:!2>||||||||||||||||||||||||</rainbow>!".miniMessage())
                    .bui

                sender.inventory.addItem(itemBuilder)

            }.register()
    }

}
