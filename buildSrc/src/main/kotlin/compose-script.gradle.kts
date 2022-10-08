import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.compose")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://androidx.dev/storage/compose-compiler/repository")
}

dependencies {
    implementation(compose.desktop.currentOs)
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class) implementation(compose.material3)
    implementation("br.com.devsrsouza.compose.icons.jetbrains:feather:${BuildConstants.composeIconVersion}")
    implementation("br.com.devsrsouza.compose.icons.jetbrains:tabler-icons:${BuildConstants.composeIconVersion}")
    implementation("com.alialbaali.kamel:kamel-image:${BuildConstants.kamelVersion}")
}