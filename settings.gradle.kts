pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "lynx"

include(
    ":server",
    ":core",
    ":common",
    ":modules:items",
    ":modules:nick",
    ":modules:npc",
    ":modules:event",
    ":modules:logger",
    ":modules:scheduler",
    ":modules:sql",
    ":modules:scoreboard",
    ":modules:tab",
    ":modules:display",
    ":nms:v1_21_4",
    ":nms:v1_21_5"
)
