plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.nyon:headquarters-connector-modrinth:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-core:${BuildConstants.connectorVersion}")
    implementation("io.ktor:ktor-server-auth:${BuildConstants.ktorVersion}")
}