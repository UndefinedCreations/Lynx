plugins {
    id("setup")
    id("shadow-convention")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(project(":core"))
    compileOnly(project(":common"))
}