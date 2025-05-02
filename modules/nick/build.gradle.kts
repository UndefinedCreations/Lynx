plugins {
    id("setup")
    `publishing-convention`
}

dependencies {
    compileOnly(libs.spigot)
    api(project(":common"))
    api(project(":nmsManager"))
}