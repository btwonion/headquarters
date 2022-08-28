plugins {
    kotlin("jvm")
    application
    `kotlin-project-script`
    `mongo-script`
    `realms-script`
    `websocket-client-script`
    `websocket-server-script`
}

dependencies {
    implementation(project(":${BuildConstants.projectName}-api"))
}

application {
    mainClass.set("dev.nyon.headquarter.manager.Manager")
}