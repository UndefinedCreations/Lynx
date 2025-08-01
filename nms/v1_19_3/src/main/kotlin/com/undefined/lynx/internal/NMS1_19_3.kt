package com.undefined.lynx.internal

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import com.undefined.lynx.LynxConfig
import com.undefined.lynx.Skin
import com.undefined.lynx.exception.UnsupportedFeatureException
import com.undefined.lynx.nms.*
import com.undefined.lynx.npc.Pose
import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.team.NameTagVisibility
import com.undefined.lynx.util.getPrivateMethod
import net.minecraft.ChatFormatting
import net.minecraft.core.GlobalPos
import net.minecraft.network.Connection
import net.minecraft.network.PacketListener
import net.minecraft.network.PacketSendListener
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.MinecraftServer
import net.minecraft.server.ServerScoreboard
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Team
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.bukkit.*
import org.bukkit.craftbukkit.v1_19_R2.CraftServer
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_19_R2.scoreboard.CraftScoreboard
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Scoreboard
import java.net.SocketAddress
import java.util.*

@Suppress("NAME_SHADOWING")
object NMS1_19_3: NMS, Listener {

    private var idMap: HashMap<UUID, UUID> = hashMapOf()

    private val clicks: MutableList<EntityInteract.() -> Unit> = mutableListOf()

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
        val cooldownMap: MutableList<Int> = mutableListOf()
        val serverPlayer = player.serverPlayer()
        val connection = Mapping1_19_3.connection.get(serverPlayer.connection) as Connection
        val channel = connection.channel
        val pipeline = channel.pipeline()
        pipeline.addBefore("packet_handler", idMap[player.uniqueId].toString(), DuplexHandler(
            {
                if (this is ServerboundInteractPacket) {
                    val entityID = Mapping1_19_3.serverBoundInteractPacketEntityId.get(this) as Int
                    val action = Mapping1_19_3.serverBoundInteractPacketAction.get(this)
                    val actionType = action::class.java.getPrivateMethod(Mapping1_19_3.ServerboundInteractionPacket_GET_TYPE)
                        .invoke(action)
                    when (actionType.toString()) {
                        "ATTACK" -> for (run in clicks) run(EntityInteract(entityID, ClickType.LEFT, player))
                        "INTERACT" -> {
                            if (cooldownMap.contains(entityID)) {
                                cooldownMap.remove(entityID)
                                return@DuplexHandler
                            }
                            cooldownMap.add(entityID)
                            for (run in clicks) run(EntityInteract(entityID, ClickType.RIGHT, player))
                        }
                    }
                }
            }
        ))
    }

    private fun endPacketListener(player: Player) {
        val serverPlayer = player.serverPlayer()
        val connection = Mapping1_19_3.connection.get(serverPlayer.connection) as Connection
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
                    this.set(skullMeta, gameProfile)
                }
                return skullMeta
            }
        }
    }
    override val playerMeta: NMS.PlayerMeta by lazy {
        object : NMS.PlayerMeta {
            override fun sendClientboundPlayerInfoRemovePacketList(
                uuid: List<UUID>,
                players: List<Player>
            ) = players.sendPackets(ClientboundPlayerInfoRemovePacket(uuid))


            override fun sendClientboundPlayerInfoAddPacket(
                player: Any,
                players: List<Player>
            ) = players.sendPackets(
                ClientboundPlayerInfoUpdatePacket(
                    ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                    player as ServerPlayer
                )
            )

            override fun sendClientboundPlayerInfoRemovePacketListServerPlayer(
                players: List<Any>,
                viewers: List<Player>
            ) = viewers.sendPackets(
                ClientboundPlayerInfoRemovePacket(
                    players.filterIsInstance<ServerPlayer>().map { it.uuid })
            )

            override fun sendClientboundPlayerInfoAddPacketPlayer(
                player: Player,
                players: List<Player>
            ) = sendClientboundPlayerInfoAddPacket(player.serverPlayer(), players)

            override fun sendClientboundPlayerInfoUpdateListedPacket(
                player: Any,
                players: List<Player>
            ) = players.sendPackets(
                ClientboundPlayerInfoUpdatePacket(
                    ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                    player as ServerPlayer
                )
            )

            override fun sendClientboundPlayerInfoUpdateListedPacketPlayer(
                player: Player,
                players: List<Player>
            ) = sendClientboundPlayerInfoUpdateListedPacket(player.serverPlayer(), players)

            override fun sendClientboundPlayerInfoUpdateListedOrderPacket(
                player: Any,
                players: List<Player>
            ) {
                val serverPlayer = player as? ServerPlayer ?: return
                players.sendPackets(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, serverPlayer))
                players.sendPackets(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, serverPlayer))
            }

            override fun sendClientboundPlayerInfoUpdateLatencyPacket(
                players: List<Any>,
                viewers: List<Player>
            ) {
                for (player in players.filterIsInstance<ServerPlayer>()) viewers.sendPackets(
                    ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY, player)
                )
            }

            override fun setName(
                player: Any,
                name: String
            ) {
                val serverPlayer = player as? ServerPlayer ?: return
                val gameProfile = serverPlayer.gameProfile
                gameProfile::class.java.getDeclaredField("name").run {
                    isAccessible = true
                    set(gameProfile, name)
                }
            }

            override fun setSkin(
                player: Any,
                texture: String,
                signature: String
            ) {
                val serverPlayer = player as? ServerPlayer ?: return
                val gameProfile = serverPlayer.gameProfile
                val properties = gameProfile.properties
                val property = properties.get("textures").iterator().next()
                properties.remove("textures", property)
                properties.put("textures", Property("textures", texture, signature))
            }

            override fun setLatency(player: Any, latency: Int) {
                val serverPlayer = player as? ServerPlayer ?: return
                serverPlayer.latency = latency
            }
        }
    }
    override val nick: NMS.Nick by lazy {
        object : NMS.Nick {
            override fun setSkin(player: Player, texture: String, signature: String) = playerMeta.setSkin(player.serverPlayer(), texture, signature)

            override fun setName(player: Player, name: String) = playerMeta.setName(player.serverPlayer(), name)

            override fun getSkin(player: Player): Skin {
                val gameProfile = player.serverPlayer().gameProfile
                val property = gameProfile.properties["textures"].iterator().next()
                return Skin(
                    property.value as String,
                    property.signature as String
                )
            }

            override fun sendClientboundRespawnPacket(player: Player) = player.serverPlayer().run {
                val serverLevel = level as ServerLevel
                this.connection.send(ClientboundRespawnPacket(
                    serverLevel.dimensionTypeId(),
                    serverLevel.dimension(),
                    0,
                    gameMode.gameModeForPlayer,
                    null,
                    false,
                    false,
                    0,
                    Optional.of(GlobalPos.of(serverLevel.dimension(), this.blockPosition()))
                ))
            }

            override fun sendClientboundGameEventPacket(player: Player)  {
                player.sendPackets(
                    ClientboundGameEventPacket(ClientboundGameEventPacket.IMMEDIATE_RESPAWN, 0f)
                )
            }

            override fun updateAbilities(player: Player) = player.serverPlayer().onUpdateAbilities()
        }
    }
    override val npc: NMS.NPC by lazy {

        val emptyConnection = object : Connection(null) {
            init {
                channel = EmptyChannel(null)
                address = object : SocketAddress() {}
            }
            override fun isConnected(): Boolean = true
            override fun send(packet: Packet<*>) {}
            override fun send(packet: Packet<*>, genericfuturelistener: PacketSendListener?) {}
            override fun setListener(pl: PacketListener?) {}
        }

        object : NMS.NPC {
            override fun removeEntityId(serverPlayer: Any) {}
            override fun createServerPlayer(name: String, texture: String, signature: String): Any {
                val gameProfile = GameProfile(UUID.randomUUID(), name)
                gameProfile.properties.put("textures", Property("textures", texture, signature))

                val server = getServer()
                val serverLevel = getServerLevel()

                val fakeServerPlayer = ServerPlayer(server, serverLevel, gameProfile)

                val connection = ServerGamePacketListenerImpl(
                    server,
                    emptyConnection,
                    fakeServerPlayer
                )

                fakeServerPlayer.connection = connection

                return fakeServerPlayer
            }

            override fun getName(serverPlayer: Any): String  {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                return serverPlayer.gameProfile.name
            }

            override fun sendClientboundPlayerInfoUpdatePacketAddPlayer(
                serverPlayer: Any,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer))
            }

            override fun sendClientboundSetEntityDataPacket(
                serverPlayer: Any,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                val data = serverPlayer.entityData
                val bitmask: Byte = (0x01 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40 or 127).toByte()
                data.set(EntityDataAccessor(17, EntityDataSerializers.BYTE), bitmask)
                val metaDataPacket = data.nonDefaultValues?.let { ClientboundSetEntityDataPacket(serverPlayer.id, it) }
                players.sendPackets(metaDataPacket)
            }

            override fun onClick(consumer: EntityInteract.() -> Unit) {
                clicks.add(consumer)
            }

            override fun setItem(serverPlayer: Any, slot: org.bukkit.inventory.EquipmentSlot, itemStack: ItemStack?, players: List<Player>) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(ClientboundSetEquipmentPacket(
                    serverPlayer.id,
                    mutableListOf(
                        Pair(
                            EquipmentSlot.entries.filter { it.name == slot.name }.getOrNull(0),
                            CraftItemStack.asNMSCopy(itemStack ?: ItemStack(Material.AIR))
                        )
                    )
                ))
            }

            override fun getUUID(serverPlayer: Any): UUID = (serverPlayer as Entity).uuid

            override fun getID(serverPlayer: Any): Int = (serverPlayer as Entity).id

            override fun sendTeleportPacket(serverPlayer: Any, players: List<Player>) {
                val serverPlayer = serverPlayer as? Entity ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(ClientboundTeleportEntityPacket(serverPlayer))
            }

            override fun setScale(serverPlayer: Any, scale: Double) {
                throw UnsupportedFeatureException("Scale Attribute")
            }

            override fun sendUpdateAttributesPacket(
                serverPlayer: Any,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(ClientboundUpdateAttributesPacket(serverPlayer.id, serverPlayer.attributes.dirtyAttributes))
            }

            override fun setPos(
                serverPlayer: Any,
                pose: Pose
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                serverPlayer.entityData.set(Mapping1_19_3.DATA_POSE, net.minecraft.world.entity.Pose.entries.first { it.name == pose.name })
            }

            override fun setGravity(
                serverPlayer: Any,
                gravity: Boolean
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                serverPlayer.entityData.set(Mapping1_19_3.DATA_NO_GRAVITY, gravity)
            }

            override fun sendClientboundMoveEntityPacketPosRot(
                serverPlayer: Any,
                deltaX: Short,
                deltaY: Short,
                deltaZ: Short,
                deltaYaw: Byte,
                deltaPitch: Byte,
                onGround: Boolean,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(
                    ClientboundMoveEntityPacket.PosRot(
                        serverPlayer.id,
                        deltaX,
                        deltaY,
                        deltaZ,
                        deltaYaw,
                        deltaPitch,
                        onGround
                    )
                )
            }

            override fun sendClientboundRotationPacket(
                serverPlayer: Any,
                deltaYaw: Byte,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(ClientboundRotateHeadPacket(serverPlayer, deltaYaw))
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
            ): Any  {
                return Objective(
                    (scoreboard as CraftScoreboard).handle,
                    UUID.randomUUID().toString(),
                    ObjectiveCriteria.DUMMY,
                    CraftChatMessage.fromJSONOrNull(title) ?: throw IllegalArgumentException("Can't get component"),
                    ObjectiveCriteria.RenderType.INTEGER
                )
            }

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
                players.sendPackets(ClientboundSetDisplayObjectivePacket(1, objective))
            }

            override fun sendSetScorePacket(
                orderId: String,
                text: String,
                objective: Any,
                score: Int,
                players: List<Player>
            ) {
                val objective = objective as? Objective ?: return
                players.sendPackets(ClientboundSetScorePacket(
                    ServerScoreboard.Method.CHANGE,
                    objective.name,
                    orderId,
                    0
                ))
            }

            override fun sendClientboundResetScorePacket(
                text: String,
                objective: Any,
                players: List<Player>
            ) {
                throw UnsupportedFeatureException("Remove sidebar line")
            }

            override fun createTeam(scoreboard: Scoreboard, name: String): Any = PlayerTeam((scoreboard as CraftScoreboard).handle, name)

            override fun setTeamPrefix(team: Any, prefix: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                Mapping1_19_3.teamSetPrefix.set(team, CraftChatMessage.fromJSONOrNull(prefix))
            }

            override fun setTeamSuffix(team: Any, suffix: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                Mapping1_19_3.teamSetSuffix.set(team, CraftChatMessage.fromJSONOrNull(suffix))
            }

            override fun setTeamSeeFriendlyInvisibles(team: Any, canSee: Boolean) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.setSeeFriendlyInvisibles(canSee)
            }

            override fun setTeamNameTagVisibility(team: Any, visible: NameTagVisibility) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.nameTagVisibility = Team.Visibility.entries.first { it.id == visible.nmsId }
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

            override fun removeTeamEntry(team: Any, name: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.players.remove(name)
            }

            override fun getTeamEntry(team: Any): List<String> {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                return team.players.toList()
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
    override val entity: NMS.Entity by lazy {
        object : NMS.Entity {
            override fun setEntityLocation(display: Any, location: Location) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.setPos(location.x, location.y, location.z)
                Mapping1_19_3.entitySetRot.invoke(display, location.yaw, location.pitch)
            }

            override fun createServerEntity(display: Any, world: World): Any? {
                return null
            }

            override fun sendClientboundAddEntityPacket(
                display: Any,
                serverEntity: Any?,
                players: List<Player>
            ) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                players.sendPackets(display.addEntityPacket)
            }
            override fun updateEntityData(display: Any, players: List<Player>) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                val pack = display.entityData.packDirty() ?: return
                players.sendPackets(ClientboundSetEntityDataPacket(display.id, pack))
            }
            override fun sendClientboundRemoveEntitiesPacket(display: Any, players: List<Player>) {
                val entity = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Entity")
                players.sendPackets(ClientboundRemoveEntitiesPacket(entity.id))
            }
        }
    }
    override val display: NMS.Display
        get() = throw UnsupportedFeatureException("Display Entities")
}

private fun Player.serverPlayer(): ServerPlayer = (this as CraftPlayer).handle
private fun Player.sendPackets(vararg packets: Packet<*>?) {
    val connection = serverPlayer().connection
    packets.filterNotNull().forEach { connection.send(it) }
}
private fun Collection<Player>.sendPackets(vararg packet: Packet<*>?) {
    for (player in this) {
        player.sendPackets(*packet)
    }
}