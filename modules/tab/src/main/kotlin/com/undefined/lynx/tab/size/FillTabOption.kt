package com.undefined.lynx.tab.size

import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.tab.TabLatency
import net.kyori.adventure.text.Component

object FillTabOption {

    internal var fakeName: String = ""
    internal var latency: TabLatency = TabLatency.BAR_1

    @JvmStatic
    fun setFillNameString(fillName: String) {
        fakeName = fillName.toJson()
    }
    @JvmStatic
    fun setFillName(fillName: Component) {
        fakeName = fillName.toJson()
    }
    @JvmStatic
    fun setLatency(tabLatency: TabLatency) {
        latency = tabLatency
    }

}