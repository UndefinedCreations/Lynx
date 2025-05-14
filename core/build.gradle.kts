import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("setup")
//    id("publishing-convention")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(project(":nms:v1_21_4"))

    implementation(project(":common"))
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