package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.itembuilder.ItemBuilder
import com.undefined.lynx.itembuilder.meta.armor.ArmorMeta
import com.undefined.lynx.nick.setName
import com.undefined.lynx.scheduler.repeatingTask
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.sidebar.team.team
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import net.kyori.adventure.text.Component
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

        val bar = sidebar(!"<red>test", async = true) {
//            addLine(!"test")
            addDynamicPlayerTimerLine("id", 1) { !"<gold>Ping: <gray>${this.inventory.getItem(EquipmentSlot.HAND)?.type?.name}" }
            addEmptyLine()
            addDynamicPlayerTimerLine("test", 1) { !"<gold>Test: <gray>${this.inventory.getItem(EquipmentSlot.HAND)?.type?.name}" }
            addDynamicPlayerTimerLine("as", 1) { !"<gold>Ads: <gray>${this.inventory.getItem(EquipmentSlot.HAND)?.type?.name}" }
        }

        val team = team {
            prefix = !"Testing "
            color = ChatColor.RED
        }

        StellarCommand("test")
            .addExecution<Player> {
                bar.addViewer(sender)
                team.addEntry(sender)
            }.register()
    }

}

operator fun String.not(): Component = MiniMessage.miniMessage().deserialize(this)