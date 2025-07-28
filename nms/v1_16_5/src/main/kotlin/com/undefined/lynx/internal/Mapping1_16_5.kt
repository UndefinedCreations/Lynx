package com.undefined.lynx.internal

import net.minecraft.server.v1_16_R3.DataWatcherObject
import net.minecraft.server.v1_16_R3.Entity
import net.minecraft.server.v1_16_R3.EntityPose
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity
import net.minecraft.server.v1_16_R3.PlayerConnection
import net.minecraft.server.v1_16_R3.ScoreboardTeam
import java.lang.reflect.Field
import java.lang.reflect.Method

object Mapping1_16_5 {

    val connection: Field = PlayerConnection::class.java.getDeclaredField("a").apply { isAccessible = true }
    val serverBoundInteractPacketEntityId: Field = PacketPlayInUseEntity::class.java.getDeclaredField("a").apply { isAccessible = true }
    val serverBoundInteractPacketAction: Field = PacketPlayInUseEntity::class.java.getDeclaredField("b").apply { isAccessible = true }
    val entitySetRot: Method = Entity::class.java.getDeclaredMethod("a", Float::class.java,
        Float::class.java).apply { isAccessible = true }
    const val ServerboundInteractionPacket_GET_TYPE = "a"

    val teamSetPrefix: Field = ScoreboardTeam::class.java.getDeclaredField("h").apply { isAccessible = true }
    val teamSetSuffix: Field = ScoreboardTeam::class.java.getDeclaredField("i").apply { isAccessible = true }

    val DATA_POSE = getAccessor<EntityPose>(Entity::class.java, "ad")
    val DATA_NO_GRAVITY = getAccessor<Boolean>(Entity::class.java, "aM")

    fun <T> getAccessor(clazz: Class<*>, string: String): DataWatcherObject<T> {
        return clazz.getDeclaredField(string).let {
            it.isAccessible = true
            return@let it.get(null) as DataWatcherObject<T>
        }
    }

}