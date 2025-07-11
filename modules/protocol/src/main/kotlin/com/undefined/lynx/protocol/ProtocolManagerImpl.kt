package com.undefined.lynx.protocol

import com.undefined.lynx.NMSManager
import com.undefined.lynx.commonProtocol.ProtocolManager
import com.viaversion.viaversion.api.Via

class ProtocolManagerImpl : ProtocolManager {

    override fun initialize() { // REFLECTION
        NMSManager.nms.protocol.onJoin = {
            println(it)
        }
    }

}