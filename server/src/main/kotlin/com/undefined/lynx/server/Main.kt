package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.itembuilder.ItemBuilder
import com.undefined.lynx.itembuilder.meta.armor.ArmorMeta
import com.undefined.lynx.nick.setName
import com.undefined.lynx.scheduler.repeatingTask
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.sidebar.team.team
import com.undefined.lynx.util.miniMessage
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)



        val team = team {
            prefix = "Testing "
            color = ChatColor.RED
        }

        StellarCommand("test")
            .addExecution<Player> {

                val bar = sidebar(!"<red>test", async = true) {
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