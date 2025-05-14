plugins {
    id("setup")
//    //id("publishing-convention")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly("io.projectreactor:reactor-core:3.8.0-M2")
    compileOnly("com.zaxxer:HikariCP:6.2.1")
}