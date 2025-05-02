plugins {
    `java-library`
    id("com.gradleup.shadow")
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["shadow"])

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

tasks {
    jar {
        archiveClassifier = "dev"
    }
    shadowJar {
        archiveClassifier = project.name
    }
}