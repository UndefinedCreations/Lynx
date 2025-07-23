import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("setup")
}

dependencies {
    compileOnly(libs.spigot)

//    api(project(":nms:v1_19_4"))
    api(project(":nms:v1_20_1"))
    api(project(":nms:v1_20_2"))
    api(project(":nms:v1_20_4"))
    api(project(":nms:v1_20_6"))
    api(project(":nms:v1_21_1"))
    api(project(":nms:v1_21_3"))
    api(project(":nms:v1_21_4"))
    api(project(":nms:v1_21_5"))
    api(project(":nms:v1_21_8"))
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