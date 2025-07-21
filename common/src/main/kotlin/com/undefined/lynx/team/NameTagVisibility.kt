package com.undefined.lynx.team

enum class NameTagVisibility(val nmsId: Int)  {
    ALWAYS(0),
    NEVER(1),
    HIDE_FOR_OTHER_TEAMS(2),
    HIDE_FOR_OWN_TEAM(3),
}