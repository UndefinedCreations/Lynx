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
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.minecraft.server.v1_16_R3.DataWatcherObject
import net.minecraft.server.v1_16_R3.DataWatcherRegistry
import net.minecraft.server.v1_16_R3.Entity
import net.minecraft.server.v1_16_R3.EntityPlayer
import net.minecraft.server.v1_16_R3.EntityPose
import org.bukkit.*
import net.minecraft.server.v1_16_R3.EnumChatFormat
import net.minecraft.server.v1_16_R3.EnumItemSlot
import net.minecraft.server.v1_16_R3.IScoreboardCriteria
import net.minecraft.server.v1_16_R3.MinecraftServer
import net.minecraft.server.v1_16_R3.NetworkManager
import net.minecraft.server.v1_16_R3.Packet
import net.minecraft.server.v1_16_R3.PacketListener
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_16_R3.PacketPlayOutRespawn
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardDisplayObjective
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardObjective
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardScore
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam
import net.minecraft.server.v1_16_R3.PacketPlayOutUpdateAttributes
import net.minecraft.server.v1_16_R3.PlayerConnection
import net.minecraft.server.v1_16_R3.PlayerInteractManager
import net.minecraft.server.v1_16_R3.ScoreboardObjective
import net.minecraft.server.v1_16_R3.ScoreboardServer
import net.minecraft.server.v1_16_R3.ScoreboardTeam
import net.minecraft.server.v1_16_R3.ScoreboardTeamBase
import net.minecraft.server.v1_16_R3.WorldServer
import org.bukkit.craftbukkit.v1_16_R3.CraftServer
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_16_R3.scoreboard.CraftScoreboard
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.net.SocketAddress
import java.util.*

@Suppress("NAME_SHADOWING")
object NMS1_16_5: NMS, Listener {

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
        val connection = Mapping1_16_5.connection.get(serverPlayer.playerConnection) as NetworkManager
        val channel = connection.channel
        val pipeline = channel.pipeline()
        pipeline.addBefore("packet_handler", idMap[player.uniqueId].toString(), DuplexHandler(
            {
                if (this is PacketPlayInUseEntity) {
                    val entityID = Mapping1_16_5.serverBoundInteractPacketEntityId.get(this) as Int
                    val action = Mapping1_16_5.serverBoundInteractPacketAction.get(this)
                    val actionType = action::class.java.getPrivateMethod(Mapping1_16_5.ServerboundInteractionPacket_GET_TYPE)
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
        val connection = Mapping1_16_5.connection.get(serverPlayer.playerConnection) as NetworkManager
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
            ) {}

            override fun sendClientboundPlayerInfoRemovePacketListServerPlayer(
                players: List<Any>,
                viewers: List<Player>
            ) = viewers.sendPackets(
                PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, players.filterIsInstance<EntityPlayer>())
            )


            override fun sendClientboundPlayerInfoAddPacket(
                player: Any,
                players: List<Player>
            ) = players.sendPackets(
                PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                    player as EntityPlayer
                )
            )

            override fun sendClientboundPlayerInfoAddPacketPlayer(
                player: Player,
                players: List<Player>
            ) = sendClientboundPlayerInfoAddPacket(player.serverPlayer(), players)

            override fun sendClientboundPlayerInfoUpdateListedPacket(
                player: Any,
                players: List<Player>
            ) {}

            override fun sendClientboundPlayerInfoUpdateListedPacketPlayer(
                player: Player,
                players: List<Player>
            ) = sendClientboundPlayerInfoUpdateListedPacket(player.serverPlayer(), players)

            override fun sendClientboundPlayerInfoUpdateListedOrderPacket(
                player: Any,
                players: List<Player>
            ) {
                val serverPlayer = player as? EntityPlayer ?: return
                players.sendPackets(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, serverPlayer))
            }

            override fun sendClientboundPlayerInfoUpdateLatencyPacket(
                players: List<Any>,
                viewers: List<Player>
            ) {
                for (player in players.filterIsInstance<EntityPlayer>()) viewers.sendPackets(
                    PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, player)
                )
            }

            override fun setName(
                player: Any,
                name: String
            ) {
                val serverPlayer = player as? EntityPlayer ?: return
                val gameProfile = serverPlayer.profile
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
                val serverPlayer = player as? EntityPlayer ?: return
                val gameProfile = serverPlayer.profile
                val properties = gameProfile.properties
                val property = properties.get("textures").iterator().next()
                properties.remove("textures", property)
                properties.put("textures", Property("textures", texture, signature))
            }

            override fun setLatency(player: Any, latency: Int) {
                val serverPlayer = player as? EntityPlayer ?: return
                serverPlayer.ping = latency
            }
        }
    }
    override val nick: NMS.Nick by lazy {
        object : NMS.Nick {
            override fun setSkin(player: Player, texture: String, signature: String) = playerMeta.setSkin(player.serverPlayer(), texture, signature)

            override fun setName(player: Player, name: String) = playerMeta.setName(player.serverPlayer(), name)

            override fun getSkin(player: Player): Skin {
                val gameProfile = player.serverPlayer().profile
                val property = gameProfile.properties["textures"].iterator().next()
                return Skin(
                    property.value as String,
                    property.signature as String
                )
            }

            override fun sendClientboundRespawnPacket(player: Player) = player.serverPlayer().run {
                val serverLevel = world as WorldServer
                player.sendPackets(
                    PacketPlayOutRespawn(
                        serverLevel.dimensionManager,
                        serverLevel.dimensionKey,
                        0,
                        playerInteractManager.gameMode,
                        null,
                        false,
                        false,
                        false
                    )
                )
            }

            override fun sendClientboundGameEventPacket(player: Player)  {
                player.sendPackets(
                    PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.l, 0f)
                )
            }

            override fun updateAbilities(player: Player) = player.serverPlayer().updateAbilities()
        }
    }
    override val npc: NMS.NPC by lazy {

        val emptyConnection = object : NetworkManager(null) {
            init {
                channel = EmptyChannel(null)
                socketAddress = object : SocketAddress() {}
            }
            override fun isConnected(): Boolean = true
            override fun sendPacket(packet: Packet<*>?) {}
            override fun sendPacket(
                packet: Packet<*>?,
                genericfuturelistener: GenericFutureListener<out Future<in Void>?>?
            ) {}
            override fun setPacketListener(packetlistener: PacketListener?) {}
        }

        object : NMS.NPC {
            override fun removeEntityId(serverPlayer: Any) {}
            override fun getName(serverPlayer: Any): String  {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                return serverPlayer.profile.name
            }

            override fun createServerPlayer(
                name: String,
                texture: String,
                signature: String
            ): Any {
                val gameProfile = GameProfile(UUID.randomUUID(), name)
                gameProfile.properties.put("textures", Property("textures", texture, signature))
                val server = getServer()
                val serverLevel = getServerLevel()
                val fakeEntityPlayer = EntityPlayer(server, serverLevel, gameProfile, PlayerInteractManager(serverLevel))

                val connection = PlayerConnection(
                    server,
                    emptyConnection,
                    fakeEntityPlayer
                )
                fakeEntityPlayer.playerConnection = connection
                return fakeEntityPlayer
            }

            override fun sendClientboundPlayerInfoUpdatePacketAddPlayer(
                serverPlayer: Any,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                players.sendPackets(
                    PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, serverPlayer)
                )
                Bukkit.getScheduler().runTaskLater(LynxConfig.javaPlugin, Runnable {
                    players.sendPackets(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, serverPlayer))
                },5)
            }

            override fun sendClientboundSetEntityDataPacket(
                serverPlayer: Any,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                val data = serverPlayer.dataWatcher
                val bitmask: Byte = (0x01 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40 or 127).toByte()
                data.set(DataWatcherObject(17, DataWatcherRegistry.a), bitmask)
                players.sendPackets(PacketPlayOutEntityMetadata(serverPlayer.id, data, true))
            }

            override fun onClick(consumer: EntityInteract.() -> Unit) {
                clicks.add(consumer)
            }

            override fun setItem(serverPlayer: Any, slot: EquipmentSlot, itemStack: ItemStack?, players: List<Player>) {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                players.sendPackets(
                    PacketPlayOutEntityEquipment(
                        serverPlayer.id,
                        mutableListOf(
                            Pair(
                                EnumItemSlot.entries.filter { it.name == slot.name }.getOrNull(0),
                                CraftItemStack.asNMSCopy(itemStack ?: ItemStack(Material.AIR))
                            )
                        )
                    )
                )
            }

            override fun getUUID(serverPlayer: Any): UUID = (serverPlayer as Entity).uniqueID

            override fun getID(serverPlayer: Any): Int = (serverPlayer as Entity).id

            override fun sendTeleportPacket(serverPlayer: Any, players: List<Player>) {
                val serverPlayer = serverPlayer as? Entity ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                players.sendPackets(PacketPlayOutEntityTeleport(serverPlayer))
            }

            override fun setScale(serverPlayer: Any, scale: Double) {
                throw UnsupportedFeatureException("Scale Attribute")
            }

            override fun sendUpdateAttributesPacket(
                serverPlayer: Any,
                players: List<Player>
            ) {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                players.sendPackets(
                    PacketPlayOutUpdateAttributes(
                        serverPlayer.id,
                        serverPlayer.attributeMap.attributes
                    )
                )
            }

            override fun setPos(
                serverPlayer: Any,
                pose: Pose
            ) {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                serverPlayer.dataWatcher.set(Mapping1_16_5.DATA_POSE, EntityPose.entries.first { it.name == pose.name })
            }

            override fun setGravity(
                serverPlayer: Any,
                gravity: Boolean
            ) {
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                serverPlayer.dataWatcher.set(Mapping1_16_5.DATA_NO_GRAVITY, gravity)
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
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                players.sendPackets(
                    PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
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
                val serverPlayer = serverPlayer as? EntityPlayer ?: throw IllegalArgumentException("Class passed was not an EntityPlayer")
                players.sendPackets(PacketPlayOutEntityHeadRotation(serverPlayer, deltaYaw))
            }

            private fun getServer(): MinecraftServer = (Bukkit.getServer() as CraftServer).server
            private fun getServerLevel() = (Bukkit.getWorlds().first() as CraftWorld).handle
        }
    }
    override val scoreboard: NMS.Scoreboard by lazy {
        object : NMS.Scoreboard {
            override fun createObjective(
                scoreboard: Scoreboard,
                title: String
            ): Any  {
                return ScoreboardObjective(
                    (scoreboard as CraftScoreboard).handle,
                    UUID.randomUUID().toString(),
                    IScoreboardCriteria.DUMMY,
                    CraftChatMessage.fromJSONOrNull(title) ?: throw IllegalArgumentException("Can't get component"),
                    IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER
                )
            }

            override fun setTitle(objective: Any, title: String) {
                val objective = objective as? ScoreboardObjective ?: return
                objective.displayName = CraftChatMessage.fromJSONOrNull(title) ?: throw IllegalArgumentException("Can't get component")
            }

            override fun sendClientboundSetObjectivePacket(
                objective: Any,
                id: Int,
                players: List<Player>
            ) {
                val objective = objective as? ScoreboardObjective ?: return
                players.sendPackets(PacketPlayOutScoreboardObjective(objective, id))
            }

            override fun sendClientboundSetDisplayObjectivePacket(
                objective: Any,
                players: List<Player>
            ) {
                val objective = objective as? ScoreboardObjective ?: return
                players.sendPackets(PacketPlayOutScoreboardDisplayObjective(1, objective))
            }

            override fun sendSetScorePacket(
                orderId: String,
                text: String,
                objective: Any,
                score: Int,
                players: List<Player>
            ) {
                val objective = objective as? ScoreboardObjective ?: return
                players.sendPackets(
                    PacketPlayOutScoreboardScore(
                        ScoreboardServer.Action.CHANGE,
                        objective.name,
                        orderId,
                        0
                    )
                )
            }

            override fun sendClientboundResetScorePacket(
                text: String,
                objective: Any,
                players: List<Player>
            ) {
                throw UnsupportedFeatureException("Remove sidebar line")
            }

            override fun createTeam(scoreboard: Scoreboard, name: String): Any =
                ScoreboardTeam((scoreboard as CraftScoreboard).handle, name)

            override fun setTeamPrefix(team: Any, prefix: String) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                Mapping1_16_5.teamSetPrefix.set(team, CraftChatMessage.fromJSONOrNull(prefix))
            }

            override fun setTeamSuffix(team: Any, suffix: String) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                Mapping1_16_5.teamSetSuffix.set(team, CraftChatMessage.fromJSONOrNull(suffix))
            }

            override fun setTeamSeeFriendlyInvisibles(team: Any, canSee: Boolean) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.setCanSeeFriendlyInvisibles(canSee)
            }

            override fun setTeamNameTagVisibility(team: Any, visible: NameTagVisibility) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.nameTagVisibility = ScoreboardTeamBase.EnumNameTagVisibility.entries.first { it.f == visible.nmsId }
            }

            override fun setTeamCollisionRule(team: Any, rule: CollisionRule) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.collisionRule = ScoreboardTeamBase.EnumTeamPush.a(rule.name)
            }

            override fun setTeamColor(team: Any, color: ChatColor) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.color = EnumChatFormat.b(color.name)
            }

            override fun addTeamEntry(team: Any, name: String) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.playerNameSet.add(name)
            }

            override fun removeTeamEntry(team: Any, name: String) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.playerNameSet.remove(name)
            }

            override fun getTeamEntry(team: Any): List<String> {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                return team.playerNameSet.toList()
            }

            override fun sendClientboundSetPlayerTeamPacketAddOrModify(
                team: Any,
                players: List<Player>
            ) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                players.sendPackets(
                    PacketPlayOutScoreboardTeam(team, 0),
                    PacketPlayOutScoreboardTeam(team, 2)
                )
            }

            override fun sendClientboundSetPlayerTeamPacketRemove(
                team: Any,
                players: List<Player>
            ) {
                val team = team as? ScoreboardTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                players.sendPackets(PacketPlayOutScoreboardTeam(team, 1))
            }

        }
    }
    override val entity: NMS.Entity by lazy {
        object : NMS.Entity {
            override fun setEntityLocation(display: Any, location: Location) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.setPosition(location.x, location.y, location.z)
                Mapping1_16_5.entitySetRot.invoke(display, location.yaw, location.pitch)
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
                players.sendPackets(display.P())
            }
            override fun updateEntityData(display: Any, players: List<Player>) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                players.sendPackets(PacketPlayOutEntityMetadata(display.id, display.dataWatcher, false))
            }
            override fun sendClientboundRemoveEntitiesPacket(display: Any, players: List<Player>) {
                val entity = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Entity")
                players.sendPackets(PacketPlayOutEntityDestroy(entity.id))
            }
        }
    }
    override val display: NMS.Display
        get() = throw UnsupportedFeatureException("Display Entities")
}

private fun Player.serverPlayer(): EntityPlayer = (this as CraftPlayer).handle
private fun Player.sendPackets(vararg packets: Packet<*>?) {
    val connection = serverPlayer().playerConnection
    packets.filterNotNull().forEach { connection.sendPacket(it) }
}
private fun Collection<Player>.sendPackets(vararg packet: Packet<*>?) {
    for (player in this) {
        player.sendPackets(*packet)
    }
}