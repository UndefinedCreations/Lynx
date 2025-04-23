import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    setup
    id("com.undefinedcreations.nova") version "0.0.4"
    id("com.gradleup.shadow")
}

repositories {
    maven {
        name = "undefined-repo"
        url = uri("https://repo.undefinedcreations.com/releases")
    }
}

dependencies {
    compileOnly(libs.spigot)

    implementation("com.undefined:stellar:0.1.68")
    implementation("com.undefined:lynx:0.0.1")

//    implementation(project(":api"))
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