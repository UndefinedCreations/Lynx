package com.undefined.lynx.internal

import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Pose
import net.minecraft.world.scores.PlayerTeam
import org.joml.Quaternionf
import org.joml.Vector3f
import java.lang.reflect.Field
import java.lang.reflect.Method

object Mapping1_21_4 {

    val connection: Field = ServerCommonPacketListenerImpl::class.java.getDeclaredField("e").apply { isAccessible = true }
    val latency: Field = ServerCommonPacketListenerImpl::class.java.getDeclaredField("o").apply { isAccessible = true }
    val serverBoundInteractPacketEntityId: Field = ServerboundInteractPacket::class.java.getDeclaredField("b").apply { isAccessible = true }
    val serverBoundInteractPacketAction: Field = ServerboundInteractPacket::class.java.getDeclaredField("c").apply { isAccessible = true }
    val entitySetRot: Method = Entity::class.java.getDeclaredMethod("b", Float::class.java,
        Float::class.java).apply { isAccessible = true }
    const val ServerboundInteractionPacket_GET_TYPE = "a"

    val teamSetPrefix: Field = PlayerTeam::class.java.getDeclaredField("g").apply { isAccessible = true }
    val teamSetSuffix: Field = PlayerTeam::class.java.getDeclaredField("h").apply { isAccessible = true }

    val DATA_POSE = getAccessor<Pose>(Entity::class.java, "aq")
    val DATA_NO_GRAVITY = getAccessor<Boolean>(Entity::class.java, "aR")

    object DISPLAY_MAPPING {
        val DATA_TRANSLATION_ID = getAccessor<Vector3f>(Display::class.java, "s")
        val DATA_SCALE_ID = getAccessor<Vector3f>(Display::class.java, "t")
        val DATA_LEFT_ROTATION_ID = getAccessor<Quaternionf>(Display::class.java, "u")
        val DATA_RIGHT_ROTATION_ID = getAccessor<Quaternionf>(Display::class.java, "ay")
        val DATA_BILLBOARD_RENDER_CONSTRAINTS_ID = getAccessor<Byte>(Display::class.java, "az")


    }

    fun <T> getAccessor(clazz: Class<*>, string: String): EntityDataAccessor<T> {
        return clazz.getDeclaredField(string).let {
            it.isAccessible = true
            return@let it.get(null) as EntityDataAccessor<T>
        }
    }

}