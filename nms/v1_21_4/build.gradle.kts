plugins {
    kotlin("jvm")
    id("com.undefinedcreations.echo")
}

repositories {
    mavenCentral()
}

dependencies {
    echo("1.21.4", printDebug = true)
    compileOnly(project(":common"))
}

