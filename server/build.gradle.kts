plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)

    kotlin("plugin.serialization") version "2.3.20"
}

group = "com.mefabc24.werewolf"
version = "1.0.0"
application {
    mainClass = "com.mefabc24.werewolf.ApplicationKt"
}

dependencies {
    val ktorVersion = "3.5.1"

    api(project(":core"))
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutinesTest)

    implementation(project(":app:shared"))
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}
