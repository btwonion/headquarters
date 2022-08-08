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

    //Kotlin
    val kotlinVersion = "1.7.10"

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))

    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    //Database
    val realmVersion = "1.0.2"
    compileOnly(pluginDep("io.realm.kotlin", realmVersion))
    runtimeOnly(pluginDep("io.realm.kotlin", realmVersion))
    implementation("io.realm.kotlin:library-base:$realmVersion")

    implementation("org.litote.kmongo:kmongo-coroutine:4.6.1")

    //Terminal
    implementation("com.github.ajalt:mordant:1.2.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")

    //Ktor
    val ktorVersion = "2.0.3"
    val logbackVersion = "1.2.10"
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    //Docker
    val dockerVersion = "3.2.13"
    implementation("com.github.docker-java:docker-java-transport-httpclient5:$dockerVersion")
    implementation("com.github.docker-java:docker-java-core:$dockerVersion")
}