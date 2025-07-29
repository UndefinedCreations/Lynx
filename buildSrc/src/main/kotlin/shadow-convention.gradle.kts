import gradle.kotlin.dsl.accessors._4a791aa2679e9704b38336063911027f.shadowJar

plugins {
    id("com.gradleup.shadow")
}

tasks {
    shadowJar {
        minimize {
            exclude("**/kotlin/**")
            exclude("**/intellij/**")
            exclude("**/jetbrains/**")
        }
        archiveClassifier = project.name
        archiveFileName = "${rootProject.name}-${project.version}.jar"
    }
}