plugins {
    `kotlin-dsl`
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

    val kotlinVersion = "1.7.10"
    val realmsVersion = "1.1.0"
    val loomVersion = "1.0-SNAPSHOT"
    val quiltFlowerVersion = "1.7.3"
    val quiltMappingsPluginVersion = "4.2.1"
    val userDevPluginVersion = "1.3.8"
    val bukkitYmlVersion = "0.5.2"

    //Kotlin
    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))

    //Database
    implementation(pluginDep("io.realm.kotlin", realmsVersion))

    //Fabric
    implementation(pluginDep("fabric-loom", loomVersion))
    implementation(pluginDep("io.github.juuxel.loom-quiltflower", quiltFlowerVersion))
    implementation(pluginDep("org.quiltmc.quilt-mappings-on-loom", quiltMappingsPluginVersion))

    //Paper
    implementation(pluginDep("io.papermc.paperweight.userdev", userDevPluginVersion))
    implementation(pluginDep("net.minecrell.plugin-yml.bukkit", bukkitYmlVersion))
}