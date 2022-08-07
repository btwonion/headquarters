plugins {
    `kotlin-dsl`
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    val kotlinVersion = "1.7.10"

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))

    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

    compileOnly(pluginDep("io.realm.kotlin", "1.0.2"))
    runtimeOnly(pluginDep("io.realm.kotlin", "1.0.2"))
    implementation("io.realm.kotlin:library-base:1.0.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("org.litote.kmongo:kmongo-coroutine:4.6.1")

    implementation("com.github.ajalt:mordant:1.2.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")
}