plugins {
    kotlin("jvm")
    id("io.realm.kotlin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.realm.kotlin:library-base:1.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
}