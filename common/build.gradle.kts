plugins {
    id("setup")
    id("com.gradleup.shadow")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly("io.netty:netty-all:4.1.94.Final")
}