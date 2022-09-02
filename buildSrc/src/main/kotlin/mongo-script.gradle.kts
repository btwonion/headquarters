plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:${BuildConstants.kmongoVersion}")
    implementation("ch.qos.logback:logback-classic:${BuildConstants.logbackVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${BuildConstants.coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${BuildConstants.serializationVersion}")
}