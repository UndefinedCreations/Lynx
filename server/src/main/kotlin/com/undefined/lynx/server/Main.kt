package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.event.event
import com.undefined.lynx.itembuilder.ItemBuilder
import com.undefined.lynx.nick.*
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import com.undefined.stellar.argument.entity.EntityDisplayType
import com.undefined.stellar.argument.world.LocationType
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit

@Suppress("UNUSED_VARIABLE")
class Main : JavaPlugin() {

    override fun onEnable() {

        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        val playerData = PlayerData()

//        val command = StellarCommand("data-uwu")
//
//        PlayerData::class.java.declaredFields.forEach {
//            it.isAccessible = true
//            println("tasd")
//            val sub = command.addArgument(it.name)
//            val set = sub.addArgument("set")
//
//            if (it.type == Int::class.java) {
//                set.addIntegerArgument("number")
//                    .addExecution<Player> {
//                        val number: Int by args
//                        it.set(playerData, number)
//                        sender.sendMessage("${it.name} has been changed")
//                    }
//            } else if (it.type == String::class.java) {
//                set.addStringArgument("string")
//                    .addExecution<Player> {
//                        val string: String by args
//                        it.set(playerData, string)
//                        sender.sendMessage("${it.name} has been changed")
//                    }
//            }
//
//            sub.addArgument("get")
//                .addExecution<Player> {
//                    sender.sendMessage("Value of ${it.name} is ${it.get(playerData)}")
//                }
//        }
//
//        command.register()

        val main = StellarCommand("nick")
            .addCooldown(5, TimeUnit.SECONDS) {
                sender.sendMessage("FUCK OFF ${TimeUnit.MILLISECONDS.toSeconds(it)}")
            }
        val nameSub= main.addArgument("name")
        nameSub.addArgument("set")
            .addStringArgument("name")
            .addExecution<Player> {
                val name: String by args
                sender.setName(ChatColor.translateAlternateColorCodes('&', name))
            }
        nameSub.addArgument("reset")
            .addExecution<Player> {
                sender.resetName()
            }
        val skinSub = main.addArgument("skin")
        skinSub.addArgument("reset")
            .addExecution<Player> {
                sender.resetSkin()
            }
        skinSub.addArgument("set")
            .addOnlinePlayersArgument("player")
            .addExecution<Player> {
                val player: Player by args
                sender.setSkin(player.getSkin())
            }
        main.register()



        StellarCommand("yeet")
            .addEntityArgument("entity", EntityDisplayType.ENTITIES)
            .addLocationArgument("vector", LocationType.LOCATION_3D)
            .addExecution<Player> {
                val entity: List<Entity> by args
                val vector: Location by args
                entity.forEach { it.velocity = it.velocity.add(vector.toVector()) }
            }.register()

    }

    override fun onDisable() {



    }

}



class PlayerData() {

    private var xp = 10
    private var money = 0
    private var uwu = "UwU"

}