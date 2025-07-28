plugins {
    id("setup")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(project(":core"))
    compileOnly(project(":common"))
}