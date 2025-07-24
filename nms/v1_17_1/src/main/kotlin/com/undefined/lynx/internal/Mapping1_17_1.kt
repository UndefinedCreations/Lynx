package com.undefined.lynx.internal

import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Pose
import net.minecraft.world.scores.PlayerTeam
import java.lang.reflect.Field
import java.lang.reflect.Method

object Mapping1_17_1 {

    val connection: Field = ServerGamePacketListenerImpl::class.java.getDeclaredField("a").apply { isAccessible = true }
    val serverBoundInteractPacketEntityId: Field = ServerboundInteractPacket::class.java.getDeclaredField("a").apply { isAccessible = true }
    val serverBoundInteractPacketAction: Field = ServerboundInteractPacket::class.java.getDeclaredField("b").apply { isAccessible = true }
    val entitySetRot: Method = Entity::class.java.getDeclaredMethod("a", Float::class.java,
        Float::class.java).apply { isAccessible = true }
    const val ServerboundInteractionPacket_GET_TYPE = "a"

    val teamSetPrefix: Field = PlayerTeam::class.java.getDeclaredField("h").apply { isAccessible = true }
    val teamSetSuffix: Field = PlayerTeam::class.java.getDeclaredField("i").apply { isAccessible = true }

    val DATA_POSE = getAccessor<Pose>(Entity::class.java, "ad")
    val DATA_NO_GRAVITY = getAccessor<Boolean>(Entity::class.java, "aM")

    fun <T> getAccessor(clazz: Class<*>, string: String): EntityDataAccessor<T> {
        return clazz.getDeclaredField(string).let {
            it.isAccessible = true
            return@let it.get(null) as EntityDataAccessor<T>
        }
    }

}