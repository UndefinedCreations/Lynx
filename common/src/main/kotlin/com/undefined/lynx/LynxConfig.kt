package com.undefined.lynx

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin

object LynxConfig {
    lateinit var javaPlugin: JavaPlugin
    private var miniMessage: MiniMessage? = null //TODO Option
    @JvmStatic
    fun setPlugin(javaPlugin: JavaPlugin) = apply { this.javaPlugin = javaPlugin }
    @JvmStatic
    fun setMiniMessage(miniMessage: MiniMessage?) = apply { this.miniMessage = miniMessage }
}