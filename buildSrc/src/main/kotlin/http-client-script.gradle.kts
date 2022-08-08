plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.0.3"
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.10")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}