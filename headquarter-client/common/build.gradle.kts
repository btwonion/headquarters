plugins {
    kotlin("jvm")
    `websocket-client-script`
    `kotlin-project-script`
}

dependencies {
    implementation(project(":${BuildConstants.projectName}-api"))
}