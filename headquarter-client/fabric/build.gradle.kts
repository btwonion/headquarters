plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `websocket-client-script`
    `fabric-build-script`
}

dependencies {
    implementation(project(":${BuildConstants.projectName}-api"))
    implementation(project(":${BuildConstants.projectName}-client:${BuildConstants.projectName}-client-common"))
}