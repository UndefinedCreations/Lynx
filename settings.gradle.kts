rootProject.name = "lynx"

include(
    ":server",
    ":api",
    ":common",
    ":nms:v1_21_4",
    ":modules:items",
    ":modules:nick",
    ":modules:npc",
    ":modules:event",
    ":modules:logger",
    ":modules:scheduler"
)
