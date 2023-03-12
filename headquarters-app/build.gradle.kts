plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `project-client-script`
    `realm-script`
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = BuildConstants.ktorVersion
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${BuildConstants.serializationVersion}")
}