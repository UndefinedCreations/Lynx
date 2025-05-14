plugins {
    id("setup")
    //id("publishing-convention")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(project(":core"))
    compileOnly(project(":common"))
}