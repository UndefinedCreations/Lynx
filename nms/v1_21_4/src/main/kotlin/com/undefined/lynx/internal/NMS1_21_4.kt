package com.undefined.lynx.internal

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import com.undefined.lynx.LynxConfig
import com.undefined.lynx.Skin
import com.undefined.lynx.nms.ClickType
import com.undefined.lynx.nms.NMS
import com.undefined.lynx.nms.NPCInteract
import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.team.NameTagVisibility
import com.undefined.lynx.util.execute
import com.undefined.lynx.util.getPrivateField
import com.undefined.lynx.util.getPrivateMethod
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.md_5.bungee.chat.ComponentSerializer
import net.minecraft.ChatFormatting
import net.minecraft.network.Connection
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.network.chat.numbers.BlankFormat
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.CommonListenerCookie
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Team
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import net.kyori.adventure.text.Component as AdventureComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_21_R3.CraftServer
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_21_R3.scoreboard.CraftScoreboard
import org.bukkit.craftbukkit.v1_21_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Scoreboard
import java.util.*

@Suppress("NAME_SHADOWING")
object NMS1_21_4: NMS, Listener {

    object MAPPING {
        const val CONNECTION = "e"
        const val ServerboundInteractPacket_ENTITYID = "b"
        const val ServerboundInteractPacket_ACTION = "c"
        const val ServerboundInteractionPacket_GET_TYPE = "a"
        const val SET_ROT = "b"
    }

    private var idMap: HashMap<UUID, UUID> = hashMapOf()
    private var cooldownMap: MutableList<Int> = mutableListOf()

    init {
        Bukkit.getOnlinePlayers().forEach { startPacketListener(it) }
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        startPacketListener(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        endPacketListener(event.player)
    }

    private fun startPacketListener(player: Player) {
        idMap[player.uniqueId] = UUID.randomUUID()
        val serverPlayer = player.serverPlayer()
        val connection = serverPlayer.connection.getPrivateField<Connection>(ServerCommonPacketListenerImpl::class.java, MAPPING.CONNECTION)
        val channel = connection.channel
        val pipeline = channel.pipeline()
        pipeline.addBefore("packet_handler", idMap[player.uniqueId].toString(), DuplexHandler(
            {
                if (this is ServerboundInteractPacket) {
                    val entityID = this.getPrivateField<Int>(ServerboundInteractPacket::class.java, MAPPING.ServerboundInteractPacket_ENTITYID)
                    val action = this.getPrivateField<Any>(ServerboundInteractPacket::class.java, MAPPING.ServerboundInteractPacket_ACTION)
                    val actionType = action::class.java.getPrivateMethod(MAPPING.ServerboundInteractionPacket_GET_TYPE).execute(action)
                    when(actionType.toString()) {
                        "ATTACK" -> {
                            clickData(NPCInteract(entityID, ClickType.LEFT, player))
                        }
                        "INTERACT" -> {
                            if (cooldownMap.contains(entityID)) {
                                cooldownMap.remove(entityID)
                                return@DuplexHandler
                            }
                            cooldownMap.add(entityID)
                            clickData(NPCInteract(entityID, ClickType.RIGHT, player))
                        }
                    }
                }
            }
        ))
    }

    private fun endPacketListener(player: Player) {
        val serverPlayer = player.serverPlayer()
        val connection = serverPlayer.connection.getPrivateField<Connection>(ServerCommonPacketListenerImpl::class.java, MAPPING.CONNECTION)
        val channel = connection.channel
        channel.eventLoop().submit {
            channel.pipeline().remove(idMap[player.uniqueId].toString())
        }
        idMap.remove(player.uniqueId)
    }

    override val itemBuilder: NMS.ItemBuilder by lazy {
        object : NMS.ItemBuilder {
            override fun setSkullTexture(skullMeta: SkullMeta, texture: String): SkullMeta {
                val gameProfile = GameProfile(UUID.randomUUID(), "texture")
                gameProfile.properties.put("textures", Property("textures", texture))
                skullMeta::class.java.getDeclaredField("profile").run {
                    this.isAccessible = true
                    this.set(skullMeta, ResolvableProfile(gameProfile))
                }
                return skullMeta
            }
        }
    }

    override val nick: NMS.Nick by lazy {
        object : NMS.Nick {
            override fun setSkin(player: Player, texture: String, signature: String) {
                val gameProfile = player.serverPlayer().gameProfile
                val properties = gameProfile.properties
                val property = properties.get("textures").iterator().next()
                properties.remove("textures", property)
                properties.put("textures", Property("textures", texture, signature))
            }

            override fun setName(player: Player, name: String) {
                val gameProfile = player.serverPlayer().gameProfile
                gameProfile::class.java.getDeclaredField("name").run {
                    isAccessible = true
                    set(gameProfile, name)
                }
            }

            override fun getSkin(player: Player): Skin {
                val gameProfile = player.serverPlayer().gameProfile
                val property = gameProfile.properties["textures"].iterator().next()
                return Skin(
                    property.value as String,
                    property.signature as String
                )
            }

            override fun sendClientboundPlayerInfoRemovePacket(player: Player) = player.sendPackets(
                ClientboundPlayerInfoRemovePacket(listOf(player.uniqueId))
            )

            override fun sendClientboundPlayerInfoAddPacket(player: Player) = player.sendPackets(
                ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, player.serverPlayer())
            )

            override fun sendClientboundPlayerInfoUpdateListedPacket(player: Player) = player.sendPackets(
                ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, player.serverPlayer())
            )

            override fun sendClientboundRespawnPacket(player: Player) = player.serverPlayer().run {
                this.connection.sendPacket(ClientboundRespawnPacket(
                    CommonPlayerSpawnInfo(
                        serverLevel().dimensionTypeRegistration(),
                        serverLevel().dimension(),
                        0,
                        gameMode.gameModeForPlayer,
                        null,
                        false,
                        false,
                        lastDeathLocation,
                        0,
                        0
                    ),
                    3
                ))
            }

            override fun sendClientboundGameEventPacket(player: Player) = player.sendPackets(
                ClientboundGameEventPacket(ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START, 0f)
            )

            override fun updateAbilities(player: Player) = player.serverPlayer().onUpdateAbilities()
        }
    }

    var clickData: NPCInteract.() -> Unit = {}

    override val npc: NMS.NPC by lazy {
        object : NMS.NPC {
            override fun createServerPlayer(name: String, texture: String, signature: String): Any {
                val gameProfile = GameProfile(UUID.randomUUID(), name)
                gameProfile.properties.put("textures", Property("textures", texture, signature))

                val server = getServer()
                val serverLevel = getServerLevel()

                val fakeServerPlayer = ServerPlayer(server, serverLevel, gameProfile, ClientInformation.createDefault())

                val connection = ServerGamePacketListenerImpl(
                    server,
                    EmptyConnection(),
                    fakeServerPlayer,
                    CommonListenerCookie.createInitial(fakeServerPlayer.gameProfile, false)
                )

                fakeServerPlayer.connection = connection

                return fakeServerPlayer
            }

            override fun sendSpawnPacket(serverPlayer: Any, location: Location, player: List<Player>?) {
                val fakeServerPlayer = serverPlayer as ServerPlayer
                fakeServerPlayer.setPos(location.x, location.y, location.z)
                fakeServerPlayer.moveTo(location.x, location.y, location.z, location.yaw, location.pitch)

                val serverEntity = ServerEntity(getServerLevel(), fakeServerPlayer, 0, false, {}, mutableSetOf())

                val addPlayer = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, fakeServerPlayer)
                val addEntity = ClientboundAddEntityPacket(fakeServerPlayer, serverEntity)

                val data = fakeServerPlayer.entityData
                val bitmask: Byte = (0x01 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40 or 127).toByte()
                data.set(EntityDataAccessor(17, EntityDataSerializers.BYTE), bitmask)
                val metaDataPacket = data.nonDefaultValues?.let { ClientboundSetEntityDataPacket(fakeServerPlayer.id, it) }

                (player ?: Bukkit.getOnlinePlayers()).sendPackets(addPlayer, addEntity, metaDataPacket)
            }

            override fun onClick(consumer: NPCInteract.() -> Unit) {
                clickData = consumer
            }

            override fun setItem(serverPlayer: Any, slot: Int, itemStack: ItemStack?, players: List<UUID>?) {
                val fakeServerPlayer = serverPlayer as ServerPlayer
                val itemStack = itemStack ?: ItemStack(Material.AIR)
                val nmsItemServer = CraftItemStack.asNMSCopy(itemStack)
                val nmsSlot = EquipmentSlot.entries.filter { it.id == slot }.getOrNull(0) ?: return
                players.sendPacket(ClientboundSetEquipmentPacket(
                    fakeServerPlayer.id,
                    mutableListOf(
                        Pair(
                            nmsSlot,
                            nmsItemServer
                        )
                    )
                ))
            }

            override fun remove(serverPlayer: Any) {
                val serverPlayer = serverPlayer as ServerPlayer
                Bukkit.getOnlinePlayers().sendPackets(ClientboundRemoveEntitiesPacket(serverPlayer.id))
            }

            override fun getUUID(serverPlayer: Any): UUID = (serverPlayer as ServerPlayer).uuid

            override fun getID(serverPlayer: Any): Int = (serverPlayer as ServerPlayer).id

            override fun sendTeleportPacket(serverPlayer: Any, location: Location, players: List<UUID>?) {
                val serverPlayer = serverPlayer as ServerPlayer
                serverPlayer.setPos(location.x, location.y, location.z)
                Entity::class.java.getPrivateMethod(MAPPING.SET_ROT, Float::class.java, Float::class.java).invoke(location.yaw, location.pitch)
                players.sendPacket(ClientboundTeleportEntityPacket(
                    serverPlayer.id,
                    PositionMoveRotation.of(serverPlayer),
                    setOf(),
                    false
                ))
            }

            private fun getServer(): MinecraftServer = (Bukkit.getServer() as CraftServer).server
            private fun getServerLevel(): ServerLevel = (Bukkit.getWorlds().first() as CraftWorld).handle
        }
    }

    override val scoreboard: NMS.Scoreboard by lazy {
        object : NMS.Scoreboard {
            override fun createObjective(
                scoreboard: Scoreboard,
                title: String
            ): Any = Objective(
                (scoreboard as CraftScoreboard).handle,
                UUID.randomUUID().toString(),
                ObjectiveCriteria.DUMMY,
                CraftChatMessage.fromJSONOrNull(title) ?: throw IllegalArgumentException("Can't get component"),
                ObjectiveCriteria.RenderType.INTEGER,
                false,
                BlankFormat.INSTANCE
            )

            override fun setTitle(objective: Any, title: String) {
                val objective = objective as? Objective ?: return
                objective.displayName = CraftChatMessage.fromJSONOrNull(title) ?: throw IllegalArgumentException("Can't get component")
            }

            override fun sendClientboundSetObjectivePacket(
                objective: Any,
                id: Int,
                players: List<Player>
            ) {
                val objective = objective as? Objective ?: return
                players.sendPackets(ClientboundSetObjectivePacket(objective, id))
            }

            override fun sendClientboundSetDisplayObjectivePacket(
                objective: Any,
                players: List<Player>
            ) {
                val objective = objective as? Objective ?: return
                players.sendPackets(ClientboundSetDisplayObjectivePacket(net.minecraft.world.scores.DisplaySlot.SIDEBAR, objective))
            }

            override fun sendSetScorePacket(
                orderId: String,
                text: String,
                objective: Any,
                score: Int,
                players: List<Player>
            ) {
                val objective = objective as? Objective ?: return
                val nmsText = CraftChatMessage.fromJSONOrNull(text) ?: throw IllegalArgumentException("Can't get component")
                players.sendPackets(ClientboundSetScorePacket(
                    orderId,
                    objective.name,
                    score,
                    Optional.of(nmsText),
                    Optional.of(BlankFormat.INSTANCE)
                ))
            }

            override fun sendClientboundResetScorePacket(
                text: String,
                objective: Any,
                players: List<Player>
            ) {
                val objective = objective as? Objective ?: return
                players.sendPackets(ClientboundResetScorePacket(text, objective.name))
            }

            override fun createTeam(scoreboard: Scoreboard, name: String): Any = PlayerTeam((scoreboard as CraftScoreboard).handle, name)

            override fun setTeamPrefix(team: Any, prefix: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.playerPrefix = CraftChatMessage.fromJSONOrNull(prefix)
            }

            override fun setTeamSuffix(team: Any, suffix: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.playerSuffix = CraftChatMessage.fromJSONOrNull(suffix)
            }

            override fun setTeamSeeFriendlyInvisibles(team: Any, canSee: Boolean) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.setSeeFriendlyInvisibles(canSee)
            }

            override fun setTeamNameTagVisibility(team: Any, visible: NameTagVisibility) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.nameTagVisibility = Team.Visibility.byName(visible.name)
            }

            override fun setTeamCollisionRule(team: Any, rule: CollisionRule) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.collisionRule = Team.CollisionRule.byName(rule.name)
            }

            override fun setTeamColor(team: Any, color: ChatColor) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.color = ChatFormatting.getByName(color.name)
            }

            override fun addTeamEntry(team: Any, name: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.players.add(name)
            }

            override fun sendClientboundSetPlayerTeamPacketAddOrModify(
                team: Any,
                players: List<Player>
            ) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                players.sendPackets(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true))
            }

            override fun sendClientboundSetPlayerTeamPacketRemove(
                team: Any,
                players: List<Player>
            ) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                players.sendPackets(ClientboundSetPlayerTeamPacket.createRemovePacket(team))
            }

        }
    }

}

private fun List<UUID>?.sendPacket(vararg packets: Packet<*>) = (this?.mapNotNull { Bukkit.getPlayer(it) } ?: Bukkit.getOnlinePlayers()).sendPackets(*packets)

private fun Player.serverPlayer(): ServerPlayer = (this as CraftPlayer).handle

private fun Player.sendPackets(vararg packets: Packet<*>?) {
    val connection = serverPlayer().connection
    packets.filterNotNull().forEach { connection.sendPacket(it) }
}

private fun Collection<Player>.sendPackets(vararg packet: Packet<*>?) {
    for (player in this) {
        player.sendPackets(*packet)
    }
}

fun AdventureComponent.toNMS(): MutableComponent = MinecraftComponentSerializer.get().serialize(this) as MutableComponent