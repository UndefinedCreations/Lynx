import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    setup
    id("com.undefinedcreations.nova") version "0.0.5"
    id("com.gradleup.shadow")
}

repositories {
    maven {
        name = "undefined-releases"
        url = uri("https://repo.undefinedcreations.com/releases")
    }
}

dependencies {
    compileOnly(libs.spigot)

    implementation("com.undefined:stellar:1.0.0")
//    implementation("com.undefined:lynx:0.0.12:core")
//    implementation("com.undefined:lynx:0.0.12:npc")
    implementation("com.undefined:lynx:0.0.21")

//    implementation(project(":common"))
//    implementation(project(":modules:npc"))

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
    runServer {
        minecraftVersion("1.21.4")
        acceptMojangEula()
    }
}

java {
    disableAutoTargetJvm()
}

kotlin {
    jvmToolchain(21)
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }
}