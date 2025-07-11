package com.undefined.lynx.internal

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.undefined.lynx.LynxConfig
import com.undefined.lynx.Skin
import com.undefined.lynx.nms.ClickType
import com.undefined.lynx.nms.EntityInteract
import com.undefined.lynx.nms.NMS
import com.undefined.lynx.team.CollisionRule
import com.undefined.lynx.team.NameTagVisibility
import com.undefined.lynx.util.execute
import com.undefined.lynx.util.getPrivateField
import com.undefined.lynx.util.getPrivateMethod
import com.undefined.lynx.util.setPrivateField
import net.minecraft.ChatFormatting
import net.minecraft.network.Connection
import net.minecraft.network.chat.numbers.BlankFormat
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks
import net.minecraft.network.protocol.game.*
import net.minecraft.network.protocol.handshake.ClientIntentionPacket
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
import net.minecraft.util.Brightness
import net.minecraft.world.entity.*
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Team
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_21_R3.CraftServer
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld
import org.bukkit.craftbukkit.v1_21_R3.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_21_R3.scoreboard.CraftScoreboard
import org.bukkit.craftbukkit.v1_21_R3.util.CraftChatMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scoreboard.Scoreboard
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.*

@Suppress("NAME_SHADOWING")
object NMS1_21_4: NMS, Listener {

    object MAPPING {
        const val CONNECTION = "e"
        const val LATENCY = "o"
        const val ServerboundInteractPacket_ENTITYID = "b"
        const val ServerboundInteractPacket_ACTION = "c"
        const val ServerboundInteractionPacket_GET_TYPE = "a"
        const val SET_ROT = "b"

        const val SET_PREFIX = "g"
        const val SET_SUFFIX = "h"
    }

    object DISPLAY_MAPPING {
        val DATA_TRANSLATION_ID = getAccessor<Vector3f>("s")
        val DATA_SCALE_ID = getAccessor<Vector3f>("t")
        val DATA_LEFT_ROTATION_ID = getAccessor<Quaternionf>("u")
        val DATA_RIGHT_ROTATION_ID = getAccessor<Quaternionf>("ay")
        val DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = getAccessor<Byte>("az")

        fun <T> getAccessor(string: String): EntityDataAccessor<T> {
            return Display::class.java.getDeclaredField(string).let {
                it.isAccessible = true
                return@let it.get(null) as EntityDataAccessor<T>
            }
        }
    }

    private var idMap: HashMap<UUID, UUID> = hashMapOf()
    private var cooldownMap: MutableList<Int> = mutableListOf()

    private val clicks: MutableList<EntityInteract.() -> Unit> = mutableListOf()

    init {
        Bukkit.getOnlinePlayers().forEach { startPacketListener(it) }
        Bukkit.getPluginManager().registerEvents(this, LynxConfig.javaPlugin)
    }

    @EventHandler
    fun onJoin(event: PlayerLoginEvent) {
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
                when(this) {
                    is ServerboundInteractPacket -> {
                        val entityID = this.getPrivateField<Int>(ServerboundInteractPacket::class.java, MAPPING.ServerboundInteractPacket_ENTITYID)
                        val action = this.getPrivateField<Any>(ServerboundInteractPacket::class.java, MAPPING.ServerboundInteractPacket_ACTION)
                        val actionType = action::class.java.getPrivateMethod(MAPPING.ServerboundInteractionPacket_GET_TYPE).execute(action)
                        when(actionType.toString()) {
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
                    is ServerboundSelectKnownPacks -> {
                        println(this.knownPacks)
                    }
                }
            }, {
                when (this) {
                    is ServerboundSelectKnownPacks -> {
                        println(this.knownPacks)
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


    override val protocol: NMS.Protocol by lazy {
        object : NMS.Protocol {
            override var onJoin: (Pair<Player, Int>) -> Unit = {}
        }
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

            override fun setServerPlayerOrder(player: Any, order: Int) {
                val serverPlayer = player as? ServerPlayer ?: return
                serverPlayer.listOrder = order
            }

            override fun sendClientboundPlayerInfoUpdateListedPacketPlayer(
                player: Player,
                players: List<Player>
            ) = sendClientboundPlayerInfoUpdateListedPacket(player.serverPlayer(), players)

            override fun sendClientboundPlayerInfoUpdateListedOrderPacket(
                player: Any,
                players: List<Player>
            ) {
                val serverPlayer = player as? ServerPlayer ?: return
                players.sendPackets(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LIST_ORDER, serverPlayer))
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
                ServerCommonPacketListenerImpl::class.java.getDeclaredField(MAPPING.LATENCY).run {
                    isAccessible = true
                    set(serverPlayer.connection, latency)
                }
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

            override fun getName(serverPlayer: Any): String  {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                return serverPlayer.gameProfile.name
            }

            override fun sendSpawnPacket(serverPlayer: Any, location: Location, player: List<Player>) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                serverPlayer.setPos(location.x, location.y, location.z)
                serverPlayer.moveTo(location.x, location.y, location.z, location.yaw, location.pitch)

                val serverEntity = ServerEntity(getServerLevel(), serverPlayer, 0, false, {}, mutableSetOf())

                val addPlayer = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, serverPlayer)
                val addEntity = ClientboundAddEntityPacket(serverPlayer, serverEntity)

                val data = serverPlayer.entityData
                val bitmask: Byte = (0x01 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40 or 127).toByte()
                data.set(EntityDataAccessor(17, EntityDataSerializers.BYTE), bitmask)
                val metaDataPacket = data.nonDefaultValues?.let { ClientboundSetEntityDataPacket(serverPlayer.id, it) }

                player.sendPackets(addPlayer, addEntity, metaDataPacket)
            }

            override fun onClick(consumer: EntityInteract.() -> Unit) {
                clicks.add(consumer)
            }

            override fun setItem(serverPlayer: Any, slot: Int, itemStack: ItemStack?, players: List<UUID>) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                val itemStack = itemStack ?: ItemStack(Material.AIR)
                val nmsItemServer = CraftItemStack.asNMSCopy(itemStack)
                val nmsSlot = EquipmentSlot.entries.filter { it.id == slot }.getOrNull(0) ?: return
                players.sendPackets(ClientboundSetEquipmentPacket(
                    serverPlayer.id,
                    mutableListOf(
                        com.mojang.datafixers.util.Pair(
                            nmsSlot,
                            nmsItemServer
                        )
                    )
                ))
            }

            override fun sendRemovePacket(serverPlayer: Any, player: List<UUID>) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                player.sendPackets(ClientboundRemoveEntitiesPacket(serverPlayer.id))
            }

            override fun getUUID(serverPlayer: Any): UUID = (serverPlayer as Entity).uuid

            override fun getID(serverPlayer: Any): Int = (serverPlayer as Entity).id

            override fun sendTeleportPacket(serverPlayer: Any, location: Location, players: List<UUID>) {
                val serverPlayer = serverPlayer as? Entity ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                serverPlayer.setPos(location.x, location.y, location.z)
                Entity::class.java.getPrivateMethod(MAPPING.SET_ROT, Float::class.java, Float::class.java).invoke(location.yaw, location.pitch)
                players.sendPackets(ClientboundTeleportEntityPacket(
                    serverPlayer.id,
                    PositionMoveRotation.of(serverPlayer),
                    setOf(),
                    false
                ))
            }

            override fun setScale(serverPlayer: Any, scale: Double) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                serverPlayer.craftAttributes.getAttribute(Attribute.SCALE)?.baseValue = scale
            }

            override fun sendUpdateAttributesPacket(
                serverPlayer: Any,
                players: List<UUID>
            ) {
                val serverPlayer = serverPlayer as? ServerPlayer ?: throw IllegalArgumentException("Class passed was not an ServerPlayer")
                players.sendPackets(ClientboundUpdateAttributesPacket(serverPlayer.id, serverPlayer.attributes.attributesToUpdate))
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
                team.setPrivateField(MAPPING.SET_PREFIX, CraftChatMessage.fromJSONOrNull(prefix))
            }

            override fun setTeamSuffix(team: Any, suffix: String) {
                val team = team as? PlayerTeam ?: throw IllegalArgumentException("The team passed was not a team.")
                team.setPrivateField(MAPPING.SET_SUFFIX, CraftChatMessage.fromJSONOrNull(suffix))
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
    override val display: NMS.Display by lazy {
        object : NMS.Display {

            override fun setLocation(display: Any, location: Location) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.setPos(location.x, location.y, location.z)
                Entity::class.java.getPrivateMethod(MAPPING.SET_ROT, Float::class.java, Float::class.java).invoke(display, location.yaw, location.pitch)
            }

            override fun createServerEntity(display: Any, world: World): Any {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                return ServerEntity((world as CraftWorld).handle, display, 0, false, {}, mutableSetOf())
            }

            override fun spawn(
                display: Any,
                serverEntity: Any,
                players: List<Player>
            ) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                val serverEntity = serverEntity as? ServerEntity ?: throw IllegalArgumentException("Class passed was not an Server Entity")
                players.sendPackets(display.getAddEntityPacket(serverEntity))
            }

            override fun setScale(display: Any, vector3f: Vector3f) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.entityData.set(DISPLAY_MAPPING.DATA_SCALE_ID, vector3f)
            }
            override fun setLeftRotation(display: Any, quaternionf: Quaternionf) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.entityData.set(DISPLAY_MAPPING.DATA_LEFT_ROTATION_ID, quaternionf)
            }
            override fun setRightRotation(display: Any, quaternionf: Quaternionf) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.entityData.set(DISPLAY_MAPPING.DATA_RIGHT_ROTATION_ID, quaternionf)
            }
            override fun setTranslation(display: Any, vector3f: Vector3f) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.entityData.set(DISPLAY_MAPPING.DATA_TRANSLATION_ID, vector3f)
            }
            override fun setInterpolationDuration(display: Any, duration: Int) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.transformationInterpolationDuration = duration
            }
            override fun setInterpolationDelay(display: Any, duration: Int) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.transformationInterpolationDelay = duration
            }
            override fun setTeleportDuration(display: Any, duration: Int) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.entityData.set(Display.DATA_POS_ROT_INTERPOLATION_DURATION_ID, duration)
            }
            override fun setBillboardRender(display: Any, byte: Byte) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.entityData.set(DISPLAY_MAPPING.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, byte)
            }
            override fun setBrightnessOverride(display: Any, int: Int) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.brightnessOverride = Brightness.unpack(int)
            }
            override fun setViewRange(display: Any, view: Float) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.viewRange = view
            }
            override fun setShadowRadius(display: Any, shadowRadius: Float) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.shadowRadius = shadowRadius
            }
            override fun setShadowStrength(display: Any, shadowStrength: Float) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.shadowStrength = shadowStrength
            }
            override fun setWidth(display: Any, width: Float) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.width = width
            }
            override fun setHeight(display: Any, height: Float) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                display.height = height
            }
            override fun updateAllEntityData(display: Any, players: List<Player>) {
                val display = display as? Entity ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                val pack = display.entityData.packDirty() ?: return
                players.sendPackets(ClientboundSetEntityDataPacket(display.id, pack))
            }
            override fun removeEntityPacket(display: Any, players: List<Player>) {
                val display = display as? Display ?: throw IllegalArgumentException("Class passed was not an Display Entity")
                players.sendPackets(ClientboundRemoveEntitiesPacket(display.id))
            }

            override val textDisplay: NMS.Display.TextDisplay by lazy {
                object : NMS.Display.TextDisplay {

                    override fun createTextDisplay(world: World): Any = Display.TextDisplay(EntityType.TEXT_DISPLAY, (world as CraftWorld).handle)

                    override fun setText(display: Any, json: String) {
                        val display = display as? Display.TextDisplay ?: throw IllegalArgumentException("Class passed was not an Text Display Entity")
                        display.text = CraftChatMessage.fromJSONOrNull(json)
                    }

                    override fun setLineWidth(display: Any, width: Int) {
                        val display = display as? Display.TextDisplay ?: throw IllegalArgumentException("Class passed was not an Text Display Entity")
                        display.entityData.set(Display.TextDisplay.DATA_LINE_WIDTH_ID, width)
                    }

                    override fun setBackgroundColor(display: Any, backgroundID: Int) {
                        val display = display as? Display.TextDisplay ?: throw IllegalArgumentException("Class passed was not an Text Display Entity")
                        display.entityData.set(Display.TextDisplay.DATA_BACKGROUND_COLOR_ID, backgroundID)
                    }

                    override fun setTextOpacity(display: Any, textOpacity: Byte) {
                        val display = display as? Display.TextDisplay ?: throw IllegalArgumentException("Class passed was not an Text Display Entity")
                        display.textOpacity = textOpacity
                    }

                    override fun setStyleFlags(display: Any, styleFlags: Byte) {
                        val display = display as? Display.TextDisplay ?: throw IllegalArgumentException("Class passed was not an Text Display Entity")
                        display.flags = styleFlags
                    }

                    override fun getStyleFlag(display: Any): Byte {
                        val display = display as? Display.TextDisplay ?: throw IllegalArgumentException("Class passed was not an Text Display Entity")
                        return display.flags
                    }
                }
            }
            override val blockDisplay: NMS.Display.BlockDisplay by lazy {
                object : NMS.Display.BlockDisplay {
                    override fun createBlockDisplay(world: World): Any = Display.BlockDisplay(EntityType.BLOCK_DISPLAY, (world as CraftWorld).handle)

                    override fun setBlock(display: Any, block: BlockData) {
                        val display = display as? Display.BlockDisplay ?: throw IllegalArgumentException("Class passed was not an Block Display Entity")
                        display.blockState = (block as CraftBlockData).state
                    }
                }
            }
            override val itemDisplay: NMS.Display.ItemDisplay by lazy {
                object : NMS.Display.ItemDisplay {
                    override fun createItemDisplay(world: World): Any = Display.ItemDisplay(EntityType.ITEM_DISPLAY, (world as CraftWorld).handle)

                    override fun setItem(display: Any, itemStack: ItemStack) {
                        val display = display as? Display.ItemDisplay ?: throw IllegalArgumentException("Class passed was not an Item Display Entity")
                        display.itemStack = CraftItemStack.asNMSCopy(itemStack)
                    }
                }
            }
            override val interaction: NMS.Display.Interaction by lazy {
                object : NMS.Display.Interaction {
                    override fun createInteraction(world: World): Any = Interaction(EntityType.INTERACTION, (world as CraftWorld).handle)

                    override fun setWidth(display: Any, width: Float) {
                        val interaction = display as? Interaction ?: throw IllegalArgumentException("Class passed was not an Interaction Entity")
                        interaction.width = width
                    }

                    override fun setHeight(display: Any, height: Float) {
                        val interaction = display as? Interaction ?: throw IllegalArgumentException("Class passed was not an Interaction Entity")
                        interaction.height = height
                    }
                }
            }
        }
    }
}

private fun List<UUID>.sendPackets(vararg packet: Packet<*>) = this.mapNotNull { Bukkit.getPlayer(it) }.sendPackets(*packet)
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