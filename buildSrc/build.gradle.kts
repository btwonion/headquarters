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

    //Kotlin
    val kotlinVersion = "1.7.10"

    compileOnly(kotlin("gradle-plugin", embeddedKotlinVersion))
    runtimeOnly(kotlin("gradle-plugin", kotlinVersion))

    compileOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", embeddedKotlinVersion))
    runtimeOnly(pluginDep("org.jetbrains.kotlin.plugin.serialization", kotlinVersion))

    //Database
    implementation(pluginDep("io.realm.kotlin", "1.0.2"))

    //Fabric
    implementation(pluginDep("fabric-loom", "0.13-SNAPSHOT"))
    implementation(pluginDep("io.github.juuxel.loom-quiltflower", "1.7.3"))
    implementation(pluginDep("org.quiltmc.quilt-mappings-on-loom", "4.2.1"))

    //Paper
    implementation(pluginDep("io.papermc.paperweight.userdev", "1.3.8"))
    implementation(pluginDep("net.minecrell.plugin-yml.bukkit", "0.5.2"))
}