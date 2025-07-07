package com.undefined.lynx.tab.layout

import com.undefined.lynx.NMSManager
import com.undefined.lynx.Skin
import com.undefined.lynx.tab.DefaultTabSkin
import com.undefined.lynx.tab.TabLatency
import com.undefined.lynx.tab.TabManager.toJson
import com.undefined.lynx.tab.runRunnable
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class TabLayout @JvmOverloads constructor(
    async: Boolean = true,
    texture: String = DefaultTabSkin.TEXTURE,
    sign: String = DefaultTabSkin.SIGN,
    defaultText: String = "",
    kotlinDSL: TabLayout.() -> Unit = {}
): AbstractTabLayout(
    async,
    texture,
    sign,
    defaultText.toJson()
) {

    @JvmOverloads
    constructor(
        async: Boolean = true,
        skin: Skin,
        defaultText: String = "",
        kotlinDSL: TabLayout.() -> Unit = {}
    ): this(async, skin.texture, skin.texture, defaultText.toJson(), kotlinDSL)

    init {
        kotlinDSL()
    }

    fun setText(index: Int, text: String) = apply {
        setTextJson(index, text.toJson())
        runnable[index]?.apply { this.text = null }
    }
    fun setText(index: Int, component: Component) = apply {
        setTextJson(index, component.toJson())
        runnable[index]?.apply { this.text = null }
    }
    fun setLatency(index: Int, tabLatency: TabLatency) = apply {
        setTabLatency(index, tabLatency)
        runnable[index]?.apply { this.tabLatency = null }
    }
    fun setSkin(index: Int, texture: String, sign: String) = apply {
        setTabSkin(index, texture, sign)
        runnable[index]?.apply { this.skin = null }
    }
    fun setSkin(index: Int, skin: Skin) = apply {
        setSkin(index, skin.texture, skin.signature)
        runnable[index]?.apply { this.skin = null }
    }
    fun setPlayer(index: Int, player: Player, prefix: String = "", suffix: String = "") = apply {
        setText(index, "$prefix${player.name}${suffix}")
        setLatency(index, TabLatency.fromPing(player.ping))
        setSkin(index, NMSManager.nms.nick.getSkin(player))
    }

    private fun setPerPlayerTextLineJson(index: Int, run: Player.() -> String) = apply {
        runRunnable({
            viewers.forEach { setTextJson(index, run(it), listOf(it)) }
            val runnable = runnable[index] ?: TabLayoutRunnable.EMPTY()
            runnable.text = run
            this.runnable[index] = runnable
        }, async)
    }
    fun setPerPlayerStringTextLine(index: Int, run: Player.() -> String) = setPerPlayerTextLineJson(index) { run(this).toJson() }
    fun setPerPlayerTextLine(index: Int, run: Player.() -> Component) = setPerPlayerTextLineJson(index) { run(this).toJson() }
    fun updatePlayerTextLine(index: Int) = apply {
        val runnable = runnable[index] ?: return@apply
        runnable.text?.let { run -> viewers.forEach { setTextJson(index, run(it), listOf(it)) } }
    }
    fun updateAllPlayerTextLines() = apply {
        for (x in 0..79) updatePlayerTextLine(x)
    }

    fun setPerPlayerSkinLine(index: Int, run: Player.() -> Skin) = apply {
        viewers.forEach { run(it).run { setTabSkin(index, this.texture, this.signature, listOf(it)) }  }
        val runnable = runnable[index] ?: TabLayoutRunnable.EMPTY()
        runnable.skin = run
        this.runnable[index] = runnable
    }
    fun updatePlayerSkinLine(index: Int) = apply {
        val runnable = runnable[index] ?: return@apply
        runnable.skin?.run { setPerPlayerSkinLine(index, this) }
    }
    fun updateAllPlayerSkinLines() = apply {
        for (x in 0..79) updatePlayerSkinLine(x)
    }

    fun setPerPlayerLatencyLine(index: Int, run: Player.() -> TabLatency) = apply {
        viewers.forEach { run(it).run { setTabLatency(index, this, listOf(it)) }  }
        val runnable = runnable[index] ?: TabLayoutRunnable.EMPTY()
        runnable.tabLatency = run
        this.runnable[index] = runnable
    }
    fun updatePlayerLatencyLine(index: Int) = apply {
        val runnable = runnable[index] ?: return@apply
        runnable.tabLatency?.run { setPerPlayerLatencyLine(index, this) }
    }
    fun updateAllPlayerLatencyLines() = apply {
        for (x in 0..79) updatePlayerLatencyLine(x)
    }

    fun updateLine(index: Int) = apply {
        updatePlayerTextLine(index)
        updatePlayerSkinLine(index)
        updatePlayerLatencyLine(index)
    }

    fun updateAllLines() = apply {
        for (x in 0..79) updateLine(x)
    }

}

fun tabLayout(
    async: Boolean = true,
    texture: String = DefaultTabSkin.TEXTURE,
    sign: String = DefaultTabSkin.SIGN,
    defaultText: String = "",
    kotlinDSL: TabLayout.() -> Unit = {}
) = TabLayout(async, texture, sign, defaultText, kotlinDSL)