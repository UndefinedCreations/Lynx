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
    ":nms:v1_20_1",
    ":nms:v1_20_2",
    ":nms:v1_20_4",
    ":nms:v1_20_6",
    ":nms:v1_21_1",
    ":nms:v1_21_3",
    ":nms:v1_21_4",
    ":nms:v1_21_5",
    ":nms:v1_21_8"
)
