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
    implementation("io.ktor:ktor-client-core-jvm:2.2.1")
    implementation("io.ktor:ktor-client-apache-jvm:2.2.1")

    testImplementation("io.ktor:ktor-client-core:${BuildConstants.ktorVersion}")
    testImplementation("io.ktor:ktor-client-cio:${BuildConstants.ktorVersion}")
}