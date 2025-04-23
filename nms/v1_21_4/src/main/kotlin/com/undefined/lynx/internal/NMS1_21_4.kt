package com.undefined.lynx.internal

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.undefined.lynx.Skin
import com.undefined.lynx.internal.NMS1_21_4.sendPackets
import com.undefined.lynx.nms.NMS
import com.undefined.lynx.scheduler.delay
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundGameEventPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.component.ResolvableProfile
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object NMS1_21_4: NMS {

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

    private fun Player.serverPlayer(): ServerPlayer = (this as CraftPlayer).handle

    private fun Player.sendPackets(vararg packets: Packet<*>) {
        val connection = serverPlayer().connection
        packets.forEach { connection.sendPacket(it) }
    }

}