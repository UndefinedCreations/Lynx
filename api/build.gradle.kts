import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("setup")
    `publishing-convention`
}

val baseShadowJar by tasks.registering(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
    group = "lynx"
    description = "Create a combined JAR of all SpigotMC dependencies."

    minimize {
        exclude("**/kotlin/**")
        exclude("**/intellij/**")
        exclude("**/jetbrains/**")
    }
    archiveClassifier = ""

    from(sourceSets.map { it.output })
    configurations = project.configurations.runtimeClasspath.map { listOf(it) }.get()
}

publishing {
    publications {
        create<MavenPublication>("baseJar") {
            artifactId = rootProject.name
            artifact(baseShadowJar)

            pom {
                name = "Lynx"
                description = "A general purpose API for Java and Kotlin."
                url = "https://www.github.com/UndefinedCreations/Lynx"
                licenses {
                    license {
                        name = "MIT"
                        url = "https://mit-license.org/"
                        distribution = "https://mit-license.org/"
                    }
                }
                developers {
                    developer {
                        id = "redmagic"
                        name = "TheRedMagic"
                        url = "https://github.com/TheRedMagic/"
                    }
                    developer {
                        id = "lutto"
                        name = "StillLutto"
                        url = "https://github.com/StillLutto/"
                    }
                }
                scm {
                    url = "https://github.com/UndefinedCreations/Lynx/"
                    connection = "scm:git:git://github.com/UndefinedCreations/Lynx.git"
                    developerConnection = "scm:git:ssh://git@github.com/UndefinedCreations/Lynx.git"
                }
            }
        }
    }
}

dependencies {
    compileOnly(libs.spigot)
    api(project(":common"))
    api(project(":nms:v1_21_4"))
}

tasks {
    shadowJar {
        minimize {
            exclude("**/kotlin/**")
            exclude("**/intellij/**")
            exclude("**/jetbrains/**")
        }
        archiveClassifier = "spigot"
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