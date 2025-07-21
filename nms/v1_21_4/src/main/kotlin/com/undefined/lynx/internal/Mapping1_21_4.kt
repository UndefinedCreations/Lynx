package com.undefined.lynx.internal

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.world.entity.Display
import org.joml.Quaternionf
import org.joml.Vector3f

object Mapping1_21_4 {

    const val CONNECTION = "e"
    const val LATENCY = "o"
    const val ServerboundInteractPacket_ENTITYID = "b"
    const val ServerboundInteractPacket_ACTION = "c"
    const val ServerboundInteractionPacket_GET_TYPE = "a"
    const val SET_ROT = "b"

    const val SET_PREFIX = "g"
    const val SET_SUFFIX = "h"

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

}