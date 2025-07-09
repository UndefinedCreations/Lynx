import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm")
    id("com.undefinedcreations.nova") version "0.0.5"
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

    implementation("com.undefined:stellar:1.0.0")

    implementation(project(":"))
    implementation(project(":modules:tab"))
    implementation(project(":modules:display"))

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
}

tasks {
    compileKotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_21
    }
    compileJava {
        options.release = 21
    }
    shadowJar {
        archiveFileName = "server.jar"
    }
    runServer {
        minecraftVersion("1.21.4")
        acceptMojangEula()
        perVersionFolder(true)
    }
}

java {
    disableAutoTargetJvm()
}

kotlin {
    jvmToolchain(21)
}