package com.undefined.lynx.internal

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.Packet

class DuplexHandler(private val read: Packet<*>.() -> Unit = {}, private val write: Any.() -> Unit = {}): ChannelDuplexHandler() {
    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        if (msg == null) return super.channelRead(ctx, msg)
        if (msg is Packet<*>) {
            read.invoke(msg)
        }
        super.channelRead(ctx, msg)
    }

    override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
        if (msg == null) return super.write(ctx, msg, promise)
        write.invoke(msg)
        super.write(ctx, msg, promise)
    }
}