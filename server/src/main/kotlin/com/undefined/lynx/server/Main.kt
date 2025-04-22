package com.undefined.lynx.server

import com.undefined.lynx.itembuilder.ItemBuilder
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import com.undefined.stellar.argument.entity.EntityDisplayType
import com.undefined.stellar.argument.world.LocationType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("UNUSED_VARIABLE")
class Main : JavaPlugin() {

    override fun onEnable() {

        StellarConfig.setPlugin(this)

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


        StellarCommand("item")
            .addExecution<Player> {
                sender.inventory.addItem(ItemBuilder(Material.STONE).setDamage(10).setMaxDamage(100).build())
            }.register()

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