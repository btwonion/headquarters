plugins {
    `kotlin-dsl`
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://server.bbkr.space/artifactory/libs-release/")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://repo.papermc.io/repository/maven-public/")
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
    implementation(pluginDep("io.realm.kotlin", realmVersion))
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

    //Fabric
    implementation(pluginDep("fabric-loom", "0.13-SNAPSHOT"))
    implementation(pluginDep("io.github.juuxel.loom-quiltflower", "1.7.3"))
    implementation(pluginDep("org.quiltmc.quilt-mappings-on-loom", "4.2.1"))

    //Paper
    implementation(pluginDep("io.papermc.paperweight.userdev", "1.3.8"))
    implementation(pluginDep("net.minecrell.plugin-yml.bukkit", "0.5.2"))

    //Velocity
    implementation("com.velocitypowered:velocity-api:3.0.1")
}