import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("setup")
    id("com.gradleup.shadow")
    `maven-publish`
    id("org.jetbrains.dokka") version "2.0.0"
}

private val submodules: HashMap<String, String> = hashMapOf(
    ":core" to "core", // project name to classifier
    ":modules:event" to "event",
    ":modules:items" to "items",
    ":modules:logger" to "logger",
    ":modules:nick" to "nick",
    ":modules:npc" to "npc",
    ":modules:scheduler" to "scheduler",
    ":modules:sql" to "sql",
)

dependencies {
    compileOnly(libs.spigot)

    api(project(":core"))
    api(project(":nms:v1_21_4"))
    api(project(":modules:event"))
    api(project(":modules:items"))
    api(project(":modules:logger"))
    api(project(":modules:nick"))
    api(project(":modules:npc"))
    api(project(":modules:scheduler"))
    api(project(":modules:sql"))
    api(project(":modules:scoreboard"))

    dokkaPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.0.0")
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
    tasks.withType<DokkaTask>()

    tasks.register<Jar>("sourceJar") {
        archiveClassifier = "sources"
        from(sourceSets.main.get().allSource)
    }
}

val packageJavadoc by tasks.registering(Jar::class) {
    group = "lynx"
    archiveClassifier = "javadoc"

    dependsOn(tasks.dokkaJavadocCollector)
    from(tasks.dokkaJavadocCollector.flatMap { it.outputDirectory })
}

val packageSources by tasks.registering(Jar::class) {
    group = "lynx"
    archiveClassifier = "sources"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(subprojects.filter { it.subprojects.isEmpty() }.map { it.sourceSets.main.get().allSource })
}

publishing {
    publications {
        create<MavenPublication>("baseJar") {
            artifactId = rootProject.name
            from(components["shadow"])
            for (module in submodules)
                artifact(project(module.key).layout.buildDirectory.dir("libs").get().file("lynx-$version-${module.value}.jar")) {
                    classifier = module.value
                }

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
        create<MavenPublication>("sources") {
            artifactId = rootProject.name
            artifact(packageJavadoc)
            artifact(packageSources)

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
    repositories {
        maven {
            name = "undefined-repo"
            url = uri("https://repo.undefinedcreations.com/releases")
            credentials(PasswordCredentials::class) {
                username = System.getenv("MAVEN_NAME") ?: property("mavenUser").toString()
                password = System.getenv("MAVEN_SECRET") ?: property("mavenPassword").toString()
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    shadowJar {
        minimize {
            exclude("**/kotlin/**")
            exclude("**/intellij/**")
            exclude("**/jetbrains/**")
        }
        archiveClassifier = ""
        dependsOn(project(":core").tasks.named("shadowJar"))

        for (module in submodules)
            dependsOn(project(module.key).tasks.named("shadowJar"))
    }
}