package com.undefined.lynx.nms

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

class DuplexHandler(private val read: Any.() -> Unit = {}, private val write: Any.() -> Unit = {}): ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        msg?.let { read(it) }
        super.channelRead(ctx, msg)
    }

    override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
        msg?.let { write(it) }
        super.write(ctx, msg, promise)
    }
}