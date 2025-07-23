package com.undefined.lynx

import com.undefined.lynx.exception.UnsupportedVersionException
import com.undefined.lynx.internal.*
import com.undefined.lynx.nms.NMS
import org.bukkit.Bukkit

object NMSManager {

    val nms: NMS by lazy { versions[version]?.let { it() } ?: throw UnsupportedVersionException(versions.keys) }
    private val version by lazy { Bukkit.getBukkitVersion().split("-")[0] }
    private val versions: Map<String, () -> NMS> = mapOf(
        "1.20" to { NMS1_20_1 },
        "1.20.1" to { NMS1_20_1 },
        "1.20.2" to { NMS1_20_2 },
        "1.20.3" to { NMS1_20_4 },
        "1.20.4" to { NMS1_20_4 },
        "1.20.5" to { NMS1_20_6 },
        "1.20.6" to { NMS1_20_6 },
        "1.21" to { NMS1_21_1 },
        "1.21.1" to { NMS1_21_1 },
        "1.21.2" to { NMS1_21_3 },
        "1.21.3" to { NMS1_21_3 },
        "1.21.4" to { NMS1_21_4 },
        "1.21.5" to { NMS1_21_5 },
        "1.21.6" to { NMS1_21_8 },
        "1.21.7" to { NMS1_21_8 },
        "1.21.8" to { NMS1_21_8 },
    )

}