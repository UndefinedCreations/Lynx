plugins {
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])

            pom {
                name = "Lynx"
                description = " A simple spigot utils api."
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
                        id = "undefined"
                        name = "UndefinedCreation"
                        url = "https://github.com/UndefinedCreations/"
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "undefined-repo"
            url = uri("https://repo.undefinedcreations.com/releases")
//            credentials(PasswordCredentials::class) {
//                username = System.getenv("MAVEN_NAME") ?: property("mavenUser").toString()
//                password = System.getenv("MAVEN_SECRET") ?: property("mavenPassword").toString()
//            }
        }
    }
}

tasks {
    jar {
        archiveClassifier = "dev"
    }
}