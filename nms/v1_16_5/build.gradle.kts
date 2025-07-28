plugins {
    kotlin("jvm")
    id("com.undefinedcreations.echo")
    id("setup")
}

repositories {
    mavenCentral()
}

dependencies {
    echo("1.16.5", printDebug = true, mojangMappings = false)
    compileOnly(project(":common"))

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
}
