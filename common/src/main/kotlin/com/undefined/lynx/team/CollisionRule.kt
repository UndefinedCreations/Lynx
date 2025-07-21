@file:Suppress("unused")

package com.undefined.lynx.team

enum class CollisionRule(val nmsId: Int) {
    ALWAYS(0),
    NEVER(1),
    FOR_OTHER_TEAMS(2),
    FOR_OWN_TEAM(3),
}