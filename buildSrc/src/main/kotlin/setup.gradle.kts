import gradle.kotlin.dsl.accessors._4a791aa2679e9704b38336063911027f.shadowJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm")
    id("com.gradleup.shadow")
}

group = properties["group"]!!
version = properties["version"]!!

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks {
    compileKotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }
    compileJava {
        options.release = 8
    }
    shadowJar {
        minimize {
            exclude("**/kotlin/**")
            exclude("**/intellij/**")
            exclude("**/jetbrains/**")
        }
        archiveClassifier = project.name
        archiveFileName = "${rootProject.name}-${project.version}-${project.name}.jar"
    }
}

java {
    disableAutoTargetJvm()
}

kotlin {
    jvmToolchain(21)
}