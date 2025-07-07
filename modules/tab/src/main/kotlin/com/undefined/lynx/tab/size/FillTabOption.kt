package com.undefined.lynx.tab.size

import com.undefined.lynx.adventure.toJson
import com.undefined.lynx.tab.TabLatency
import net.kyori.adventure.text.Component

object FillTabOption {

    internal var fakeName: String = "".toJson()
    internal var latency: TabLatency = TabLatency.BAR_1

    fun setFillNameString(fillName: String) {
        fakeName = fillName.toJson()
    }

    fun setFillName(fillName: Component) {
        fakeName = fillName.toJson()
    }

    fun setLatency(tabLatency: TabLatency) {
        latency = tabLatency
    }

}