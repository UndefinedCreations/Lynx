package com.undefined.lynx.tab.size

import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.util.miniMessage
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object TabSize {
    @JvmStatic
    @JvmOverloads
    fun fillTab(
        player: Player,
        fakePlayerName: String = FillTabOption.fakeName,
        fakePlayerLatency: TabLatency = FillTabOption.latency,
        async: Boolean = true
    ) = TabSizeManager.addPlayer(player, fakePlayerName.toJson(), fakePlayerLatency, async)

    @JvmStatic
    @JvmOverloads
    fun fillTab(
        player: Player,
        fakePlayerName: Component = FillTabOption.fakeName.miniMessage(),
        fakePlayerLatency: TabLatency = FillTabOption.latency,
        async: Boolean = true
    ) = TabSizeManager.addPlayer(player, fakePlayerName.toJson(), fakePlayerLatency, async)

    @JvmStatic
    @JvmOverloads
    fun removeFilledTab(
        player: Player,
        async: Boolean = true
    ) = TabSizeManager.removePlayer(player, async)
}

fun Player.fillTab(
    async: Boolean = true,
    fakePlayerName: String = FillTabOption.fakeName,
    fakePlayerLatency: TabLatency = FillTabOption.latency
) = TabSize.fillTab(this, fakePlayerName, fakePlayerLatency, async)

fun Player.fillTab(
    fakePlayerName: Component = FillTabOption.fakeName.miniMessage(),
    fakePlayerLatency: TabLatency = FillTabOption.latency,
    async: Boolean = true
) = TabSize.fillTab(this, fakePlayerName.toJson(), fakePlayerLatency, async)

fun Player.removeFilledTab(
    async: Boolean = true
) = TabSize.removeFilledTab(this, async)