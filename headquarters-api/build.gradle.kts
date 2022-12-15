plugins {
    kotlin("jvm")
    `kotlin-project-script`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.nyon:headquarters-connector-modrinth:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-core:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-fabric:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-mojang:${BuildConstants.connectorVersion}")
}