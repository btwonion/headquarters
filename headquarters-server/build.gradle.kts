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
}