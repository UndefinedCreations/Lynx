package com.undefined.lynx

import org.bukkit.plugin.java.JavaPlugin

object LynxConfig {
    lateinit var javaPlugin: JavaPlugin
    fun setPlugin(javaPlugin: JavaPlugin) = apply { this.javaPlugin = javaPlugin }
}