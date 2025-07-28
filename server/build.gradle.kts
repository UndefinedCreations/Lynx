import com.undefinedcreations.nova.ServerType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm")
    id("com.undefinedcreations.nova") version "0.0.8"
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    }
    maven {
        name = "undefined-releases"
        url = uri("https://repo.undefinedcreations.com/releases")
    }
}

dependencies {
    compileOnly(libs.spigot)

    implementation("com.undefined:stellar:1.0.4")

    implementation(project(":"))


    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
}

tasks {
    compileKotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }
    compileJava {
        options.release = 8
    }
    shadowJar {
        archiveFileName = "server.jar"
    }
    runServer {
        minecraftVersion("1.21.4")
        serverType(ServerType.SPIGOT)
        acceptMojangEula()
        perVersionFolder(true)
    }
}

java {
    disableAutoTargetJvm()
}

kotlin {
    jvmToolchain(17)
}