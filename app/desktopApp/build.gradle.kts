import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    val ktorVersion = "3.5.1"

    implementation(project(":app:shared"))
    implementation(project(":server"))

    implementation(libs.compose.material3)
    implementation(libs.compose.components.resources)

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.resources {
    packageOfResClass = "com.mefabc24.werewolf.resources"
    generateResClass = always
}

compose.desktop {
    application {
        mainClass = "com.mefabc24.werewolf.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.mefabc24.werewolf"
            packageVersion = "1.0.0"
        }
    }
}