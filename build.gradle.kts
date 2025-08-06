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
    ":modules:playerMeta" to "playerMeta",
    ":modules:npc" to "npc",
    ":modules:scheduler" to "scheduler",
    ":modules:scoreboard" to "scoreboard",
    ":modules:tab" to "tab",
    ":modules:display" to "display",
    ":modules:kotlin" to "kotlin",
)

dependencies {
    compileOnly(libs.spigot)

    api(project(":core"))
    api(project(":modules:event"))
    api(project(":modules:items"))
    api(project(":modules:logger"))
    api(project(":modules:playerMeta"))
    api(project(":modules:npc"))
    api(project(":modules:scheduler"))
    api(project(":modules:scoreboard"))
    api(project(":modules:tab"))
    api(project(":modules:display"))
    api(project(":modules:kotlin"))
}

//val packageJavadoc by tasks.registering(Jar::class) {
//    group = "lynx"
//    archiveClassifier = "javadoc"
//
//    dependsOn(tasks.dokkaJavadocCollector)
//    from(tasks.dokkaJavadocCollector.flatMap { it.outputDirectory })
//}
//
//val packageSources by tasks.registering(Jar::class) {
//    group = "lynx"
//    archiveClassifier = "sources"
//    duplicatesStrategy = DuplicatesStrategy.INCLUDE
//    println("\n\n\n\n${subprojects.filter { it.subprojects.isEmpty() }}\n\n\n\n")
//    from(subprojects.filter { !it.subprojects.isEmpty() }.map { it.sourceSets.main.get().allSource })
//}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            artifactId = rootProject.name
            from(components["shadow"])

//            artifact(packageJavadoc)
//            artifact(packageSources)
            for (module in submodules)
                artifact(project(module.key).layout.buildDirectory.dir("libs").get().file("lynx-$version.jar")) {
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
    }
    repositories {
        maven {
            name = "undefined-releases"
            url = uri("https://repo.undefinedcreations.com/releases")
            credentials(PasswordCredentials::class) {
                username = System.getenv("MAVEN_NAME") ?: property("mavenUser").toString()
                password = System.getenv("MAVEN_SECRET") ?: property("mavenPassword").toString()
            }
        }
    }
    repositories {
        maven {
            name = "undefined-snapshots"
            url = uri("https://repo.undefinedcreations.com/snapshots")
            credentials(PasswordCredentials::class) {
                username = System.getenv("MAVEN_NAME") ?: property("mavenUser").toString()
                password = System.getenv("MAVEN_SECRET") ?: property("mavenPassword").toString()
            }
        }
    }
}

tasks {
    shadowJar {
        minimize {
            exclude("**/kotlin/**")
            exclude("**/intellij/**")
            exclude("**/jetbrains/**")
        }
        archiveClassifier = ""

        for (module in submodules) dependsOn(project(module.key).tasks.named("shadowJar"))
    }
}