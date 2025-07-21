package com.undefined.lynx

import com.undefined.lynx.exception.UnsupportedVersionException
import com.undefined.lynx.internal.NMS1_21_4
import com.undefined.lynx.internal.NMS1_21_5
import com.undefined.lynx.nms.NMS
import org.bukkit.Bukkit

object NMSManager {

    val nms: NMS by lazy { versions[version]?.let { it() } ?: throw UnsupportedVersionException(versions.keys) }
    private val version by lazy { Bukkit.getBukkitVersion().split("-")[0] }
    private val versions: Map<String, () -> NMS> = mapOf(
        "1.21.4" to { NMS1_21_4 },
        "1.21.5" to { NMS1_21_5 },
    )

}