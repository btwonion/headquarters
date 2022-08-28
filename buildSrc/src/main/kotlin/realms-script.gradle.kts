plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.realm.kotlin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.realm.kotlin:library-base:${BuildConstants.realmVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${BuildConstants.coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${BuildConstants.serializationVersion}")
}