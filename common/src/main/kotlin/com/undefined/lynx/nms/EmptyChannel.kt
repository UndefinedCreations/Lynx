package com.undefined.lynx.nms

import io.netty.channel.*
import java.net.SocketAddress

class EmptyChannel(channel: Channel?): AbstractChannel(channel) {

    private val config: ChannelConfig = DefaultChannelConfig(this)

    override fun config(): ChannelConfig {
        config.isAutoRead = true
        return config
    }

    @Throws(Exception::class)
    override fun doBeginRead() {
    }

    @Throws(Exception::class)
    override fun doBind(arg0: SocketAddress?) {
    }

    @Throws(Exception::class)
    override fun doClose() {
    }

    @Throws(Exception::class)
    override fun doDisconnect() {
    }

    @Throws(Exception::class)
    override fun doWrite(arg0: ChannelOutboundBuffer?) {
    }

    override fun isActive(): Boolean = false

    override fun isCompatible(arg0: EventLoop?): Boolean = false

    override fun isOpen(): Boolean = false

    override fun localAddress0(): SocketAddress? = null

    override fun metadata(): ChannelMetadata = ChannelMetadata(true)

    override fun newUnsafe(): AbstractUnsafe? = null

    override fun remoteAddress0(): SocketAddress? = null
}