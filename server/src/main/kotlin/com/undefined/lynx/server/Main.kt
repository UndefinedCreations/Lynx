package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.sidebar.sidebar.sidebar
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
                sidebar("<red>test") {
                    addViewer(sender)
                    addLine("${ChatColor.AQUA}1")
                    addEmptyLine()
                    addDynamicPlayerTimerLine("Test", 10) {
                        "<white>Block <aqua>${this.inventory.itemInHand.type.name}".miniMessage()
                    }
                }


                sender.fillTab()


//                val team = TabManager.createTeam()
//                TabManager.addTeamEntry(team, "0")
//                TabManager.modifyTeamName(team, ComponentSerializer.toJson(net.md_5.bungee.api.chat.TextComponent("        ")).toString(), listOf(sender))
//
//                val serverPlayer = TabManager.createFakePlayer("0", "ewogICJ0aW1lc3RhbXAiIDogMTYyNjU1MTAwMDUwMiwKICAicHJvZmlsZUlkIiA6ICIwNWJhN2FmOGY0M2M0NGFjYWJkZjkzZjVmMTk2Njg3NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJESkdyb3VuZDAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM4ZTBkZGYyNDMyZjQzMzJiODc2OTFiNTk1MmM3Njc5NzYzZWY0ZjI3NWI4NzRlOWJjZWI4ODhlZDViNWI5IgogICAgfQogIH0KfQ==", "iKYRdwSaXcuNVhVBdxU5n9KyMPt2xQ7tGFGnTiETSrxyEVg1qSeY413xW9ND6uRX6jk0Fg6X/VR0xNRF7a+YNQYekK7XqjegwshBr3bg+kLfCEA+4xhtnGEzLUzNR4HvAvtEoKGmD/Rc5kaKAk4Orej56jmNQZLkqNjAQTJBMSHlFpEMgLliKGzZwZULN2wa4Wi9xeejcZ/Ws9oxU2u0SOCKILZFnZowi/Somtkh4JEli5ut/GLlgZNZKJeoi8ZyZLmN2hrDsEjlpTMli2p21b2w40SSSvd9mMRrH7wr3xDNmp3xI/Xmcx37IBQV7mS809vTUv+QKi6yKyoUS+JygMsul/mymion+QMSs6/5pBmNELjAnMqUZuw95rzzZQAKRz4INHYIVS9yxiYgg0+GZVEnXCK6WpnCRlRQRlO5YLVdh5PlZm/Lbg4fuMsQWAMYSzx5DovAuIsmSHTA9ZPJ5otwLvCD+OhplKgfF4kJv1bMDMdiXZLv2A/w+RdqlBerI3Em7A1LxxT5vSUARQwa2ZGohC+cW6VhVEyRiMfg1wRn4So4dCH/91ElSmeVOW+dIchWdfOON26OJKGRSFU9o6hy3wjS0pr/KMCAsTP7KgiTGcdZdgaR+BgRAEa4iuBClL65dC1MXmFbFI2alVb69ZxkZhDvnzxzb7g2n9c6Mc4=")
//                TabManager.addFakePlayers(listOf(serverPlayer), listOf(sender))
//
//                for (x in 0..80) {
//
////                    TabManager.modifyFakePlayerLatency(serverPlayer, 1000, listOf(sender))
//                }
            }.register()
    }

}

operator fun String.not(): Component = MiniMessage.miniMessage().deserialize(this)