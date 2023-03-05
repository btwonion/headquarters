plugins {
    application
    `kotlin-project-script`
    `mongo-script`
    `ktor-server-script`
}

application {
    mainClass.set("dev.nyon.headquarters.server.MainKt")
}

dependencies {
    implementation(project(":headquarters-api"))
    implementation("io.ktor:ktor-client-core:${BuildConstants.ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${BuildConstants.ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${BuildConstants.ktorVersion}")

    testImplementation("dev.nyon:headquarters-connector-modrinth:${BuildConstants.connectorVersion}")
    testImplementation("dev.nyon:headquarters-connector-mojang:${BuildConstants.connectorVersion}")
    testImplementation("io.ktor:ktor-client-content-negotiation:${BuildConstants.ktorVersion}")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:${BuildConstants.ktorVersion}")
    testImplementation("io.ktor:ktor-client-core:${BuildConstants.ktorVersion}")
    testImplementation("io.ktor:ktor-client-cio:${BuildConstants.ktorVersion}")
    testImplementation("io.ktor:ktor-client-content-negotiation:${BuildConstants.ktorVersion}")
}