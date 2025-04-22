plugins {
    id("setup")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(project(":common"))
    compileOnly(project(":nms:v1_21_4"))
}