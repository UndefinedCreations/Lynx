package com.undefined.lynx.tab

enum class TabLatency(val latency: Int) {
    BAR_5(0),
    BAR_4(200),
    BAR_3(400),
    BAR_2(800),
    BAR_1(1200);

    companion object {
        @JvmStatic
        fun fromPing(ping: Int) =
            when {
                ping < 149 -> BAR_5
                ping < 299 -> BAR_4
                ping < 599 -> BAR_3
                ping < 999 -> BAR_2
                else -> BAR_1
            }
    }

}