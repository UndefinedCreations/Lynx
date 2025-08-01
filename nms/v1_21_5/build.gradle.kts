plugins {
    kotlin("jvm")
    id("com.undefinedcreations.echo")
}

repositories {
    mavenCentral()
}

dependencies {
    echo("1.21.5", printDebug = true)
    compileOnly(project(":common"))

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
}
