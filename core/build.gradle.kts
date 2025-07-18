import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("setup")
}

dependencies {
    compileOnly(libs.spigot)

    api(project(":nms:v1_21_4"))
    api(project(":common"))
}

tasks {
    shadowJar {
        exclude("**/kotlin/**")
        exclude("**/intellij/**")
        exclude("**/jetbrains/**")
        archiveClassifier = "core"
    }
    compileKotlin {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }
    compileJava {
        options.release = 8
    }
}

java {
    disableAutoTargetJvm()
}

kotlin {
    jvmToolchain(21)
}