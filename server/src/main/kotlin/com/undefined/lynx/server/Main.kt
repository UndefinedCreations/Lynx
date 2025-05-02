package com.undefined.lynx.server

import com.undefined.lynx.LynxConfig
import com.undefined.lynx.event.event
import com.undefined.stellar.StellarCommand
import com.undefined.stellar.StellarConfig
import com.undefined.stellar.argument.misc.UUIDArgument
import com.undefined.stellar.argument.world.LocationType
import com.undefined.stellar.util.unregisterCommand
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("UNUSED_VARIABLE")
class Main : JavaPlugin() {

    override fun onEnable() {

        StellarConfig.setPlugin(this)
        LynxConfig.setPlugin(this)

        unregisterCommand("item")


//        val npcCommand = StellarCommand("npc")
//
//        npcCommand.addArgument("spawn")
//            .addLocationArgument("location", LocationType.LOCATION_3D)
//            .addStringArgument("name")
//            .addOnlinePlayersArgument("skinPlayer", { true })
//            .addExecution<Player> {
//                val location: Location by args
//                val name: String by args
//                val skinPlayer: Player by args
//                val skin = skinPlayer.getSkin()
//                sender.world.spawnNPC(location, name, skin.texture, skin.signature).onClick { player.sendMessage("onClick {$clickType}") }
//
//            }
//
//        val selectedNPC = npcCommand.addListArgument(UUIDArgument("uuid"), { NPCManager.getAllNPCs().map { it.getUUID() } }, { it })
//
//
//
//        selectedNPC.addArgument("item")
//            .addIntegerArgument("slot", 0, 6)
//            .addItemStackArgument("itemStack")
//            .addExecution<Player> {
//                val uuid: UUID by args
//                val slot: Int by args
//                val itemStack: ItemStack by args
//                val npc = sender.world.getNPC(uuid)
//                npc?.setItem(slot, itemStack)
//            }
//
//        selectedNPC.addArgument("remove")
//            .addExecution<Player> {
//                val uuid: UUID by args
//                sender.world.getNPC(uuid)?.remove()
//            }
//
//        selectedNPC.addArgument("teleport")
//            .addLocationArgument("location", LocationType.LOCATION_3D)
//            .addExecution<Player> {
//                val uuid: UUID by args
//                val location: Location by args
//                sender.world.getNPC(uuid)?.teleport(location)
//            }
//
//
//        npcCommand.register()
//        val main = StellarCommand("nick")
//            .addCooldown(5, TimeUnit.SECONDS) {
//                sender.sendMessage("FUCK OFF ${TimeUnit.MILLISECONDS.toSeconds(it)}")
//            }
//        val nameSub= main.addArgument("name")
//        nameSub.addArgument("set")
//            .addStringArgument("name")
//            .addExecution<Player> {
//                val name: String by args
//                sender.setName(ChatColor.translateAlternateColorCodes('&', name))
//            }
//        nameSub.addArgument("reset")
//            .addExecution<Player> {
//                sender.resetName()
//            }
//        val skinSub = main.addArgument("skin")
//        skinSub.addArgument("reset")
//            .addExecution<Player> {
//                sender.resetSkin()
//            }
//        skinSub.addArgument("set")
//            .addOnlinePlayersArgument("player")
//            .addExecution<Player> {
//                val player: Player by args
//                sender.setSkin(player.getSkin())
//            }
//        main.register()
//
//
//
//        StellarCommand("item")
//            .addStringArgument("name")
//            .addExecution<Player> {
//                val name: String by args
//                sender.inventory.addItem(ItemBuilder(Material.GOAT_HORN)
//                    .setName(name)
//                    .build()
//                )
//            }.register()
//
//
//        val list: MutableList<Arena> = mutableListOf()

//        StellarCommand("arena")
//            .addListArgument("mapid", { list }, { uuid -> list.first { it.id.toString() == uuid } })
//            .addExecution<Player> {
//                val arena: Arena = getArgument("mapid")
//            }

//        ItemBuilder(Material.PLAYER_HEAD)
//            .setName("Name")
//            .addLore("Line")
//            .meta<SkullMeta> {
//                texture = "IDK"
//            }.build()
//
//        val nameSpace = NamespacedKey(this, "test")
//
//        event<PlayerJoinEvent> {
//
//            player.persistentDataContainer.set(nameSpace, PersistentDataType.STRING, "UwU")
//
//            sendInfo(player.persistentDataContainer.get(nameSpace, PersistentDataType.STRING) ?: "null")
//
//        }

    }

    override fun onDisable() {



    }

}
