plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://repo.nyon.dev/releases/")
}

dependencies {
    implementation("dev.nyon:headquarters-connector-modrinth:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-core:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-fabric:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-mojang:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-quilt:${BuildConstants.connectorVersion}")

    implementation("io.ktor:ktor-client-core:${BuildConstants.ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${BuildConstants.ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${BuildConstants.ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${BuildConstants.ktorVersion}")
    implementation("io.ktor:ktor-client-logging:${BuildConstants.ktorVersion}")
    implementation("ch.qos.logback:logback-classic:${BuildConstants.logbackVersion}")
}