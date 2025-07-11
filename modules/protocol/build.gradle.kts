plugins {
    id("setup")
//    //id("publishing-convention")
}

repositories {
    maven("https://repo.viaversion.com")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(project(":core"))
    compileOnly(project(":common"))
    compileOnly("com.viaversion:viaversion-api:5.4.1")
}