plugins {
    kotlin("jvm")
    application
    `kotlin-project-script`
    `mongo-script`
    `realms-script`
    `websocket-client-script`
}

dependencies {
    implementation(project(":${BuildConstants.projectName}-api"))
}
