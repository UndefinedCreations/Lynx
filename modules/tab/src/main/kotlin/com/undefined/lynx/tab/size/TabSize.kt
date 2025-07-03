package com.undefined.lynx.tab.size

import org.bukkit.entity.Player

fun Player.fillTabString(
    async: Boolean = true
) = TabSizeManager.addPlayer(
    this,
    FillTabOption.fakeName,
    FillTabOption.latency,
    async
)

fun Player.fillTab(
    async: Boolean = true
) = TabSizeManager.addPlayer(
    this,
    FillTabOption.fakeName,
    FillTabOption.latency,
    async
)

fun Player.removeFilledTab(
    async: Boolean = true
) = TabSizeManager.removePlayer(this, async)