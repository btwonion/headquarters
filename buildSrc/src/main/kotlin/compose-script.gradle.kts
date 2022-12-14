import org.jetbrains.compose.ExperimentalComposeLibrary

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
    @OptIn(ExperimentalComposeLibrary::class) implementation(compose.material3)
    implementation("br.com.devsrsouza.compose.icons.jetbrains:feather:${BuildConstants.composeIconVersion}")
    implementation("com.alialbaali.kamel:kamel-image:${BuildConstants.kamelVersion}")
    implementation("com.mikepenz:multiplatform-markdown-renderer-jvm:${BuildConstants.markdownRendererVersion}")
}