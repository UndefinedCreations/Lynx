package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.nick.getGameProfile
import com.undefined.lynx.nick.setName
import com.undefined.lynx.nick.setSkin
import com.undefined.lynx.npc.spawnNPC
import com.undefined.lynx.sidebar.sidebar.lines.UpdatablePlayerTimerLine
import com.undefined.lynx.sidebar.sidebar.sidebar
import com.undefined.lynx.tab.size.fillTab
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        val line = UpdatablePlayerTimerLine(20) { "<aqua>Block <gray>: ${it.inventory.itemInMainHand.type.name}" }
        val side = sidebar("Testing") {
            addLine(line)
            addUpdatableLine { "Testing" }
            addUpdatableLine { "Testing2" }
        }

        StellarCommand("test")
            .addExecution<Player> {

                val npc = sender.location.spawnNPC("Testing")
                npc.hideName(true)
                npc.setPerPlayerProfile { it.getGameProfile() }



                var pastLoc: Location? = null

                Bukkit.getScheduler().runTaskTimer(this@Main, Runnable {
                    if (pastLoc != null) npc.lookAt(pastLoc!!)
                    pastLoc = sender.location.clone()
                }, 1, 1)

                sender.setName("Testing")
                sender.setSkin("ewogICJ0aW1lc3RhbXAiIDogMTczNzExMzI0Mjg0OCwKICAicHJvZmlsZUlkIiA6ICIwNTAzNzZmZjAxY2I0OGVjOTUwM2NhMjhjMWU2MzlkMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJKb25haDU1OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkyOWVjZmRlMThiYTk4MmNiMGU4YjIxN2ZmYzI5YjBlZDIyODQ0NDc4MWYyNGE3MGViNTVjMGI3MWFkOTEyNSIKICAgIH0KICB9Cn0=", "NUVQR4Oxsm2gt6gm1fP/CHoQwKEqR6BNdDJtHHR1VPF9XTHqQUcEwUW9v9p5jj8ePewvprTmBU0jMrq9hpkHUfUuBoHV0Hkw+gpZf9YMy6caylEeJb0cPQVc0XOopDz8siCRas0D8TX/KVssZwlEujDuQh/cJdO/Zuc1T3m9VoX0NP0cMJvbAk+0DH+5cVOKGYhAA4htdiBje9LDahCo0aXo/uu4C0NNBDLLCl7DUZFxWoDMzP11st5OrE275+4IRG7mugX+Xk/8Qud7N09RNkSBy0SfHvoHI25DLF0iK+3cGZZUbqg0m2wcbVcOruUfK4Sd86PQ5+bLD4wS7MgJsz7dnxu/kgqveupZcD6LAw5H8UjmtiNbnmRmLFdwbRgDB2Y9te8wGWI2Ol36aNYYLaMdmBShH7KnVW1jkEssjsuhHvx1r/cf2jeNLEAYyYb+nScGDJ0ImezDH1BOWIqghkIEn7QE4gdO8QS5eNcU8gc1RkyFePucG2sjlki1N3HzKkDaueqSl4YMX8+fQpdVI/irCK9enmmoGrvxa3HfTvg6lZH+MNyyArcaz0DvWTw7DmhMf6QyFTMXlzK4MHYcjvNkVf4eIf9qSeGukUB4Me+sYf5VeyTrqP5wlLWs0QmW+bl0bYvF38Bz7E/QR7KCW8CMUr5YTHZGz8d1zSSazOY=")

                side.addViewer(sender)
                sender.fillTab(true)
            }.register()

    }

}
