package com.undefined.lynx.tab.size

import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.tab.TabManager.toJson
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

fun Player.fillTabString(
    fakeName: String = "",
    latency: TabLatency = TabLatency.BAR_5,
    async: Boolean = true
) = TabSizeManager.addPlayer(
    this,
    fakeName.toJson(),
    latency,
    async
)

fun Player.fillTab(
    fakeName: Component = Component.empty(),
    latency: TabLatency = TabLatency.BAR_5,
    async: Boolean = true
) = TabSizeManager.addPlayer(
    this,
    fakeName.toJson(),
    latency,
    async
)

fun Player.removeFilledTab(
    async: Boolean = true
) = TabSizeManager.removePlayer(this, async)