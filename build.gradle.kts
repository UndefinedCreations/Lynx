plugins {
    id("setup")
    `publishing-convention`
    id("com.gradleup.shadow") version "8.3.5"
}

dependencies {
    compileOnly(libs.spigot)
}

tasks {
    shadowJar {
        exclude("**/kotlin/**")
        archiveClassifier = ""
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}