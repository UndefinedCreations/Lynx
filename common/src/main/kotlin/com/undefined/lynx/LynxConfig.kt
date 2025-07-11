package com.undefined.lynx

import com.undefined.lynx.commonProtocol.ProtocolManager
import org.bukkit.plugin.java.JavaPlugin

object LynxConfig {
    lateinit var javaPlugin: JavaPlugin
    @JvmStatic
    fun setPlugin(javaPlugin: JavaPlugin) = apply { this.javaPlugin = javaPlugin }
    @JvmStatic
    fun initialize() = apply {
        try {
            val clazz = Class.forName("com.undefined.lynx.protocol.ProtocolManager").getConstructor().newInstance() as ProtocolManager
            clazz.initialize()
        } catch (e: Exception) {}
    }
}