plugins {
    kotlin("jvm")
    `websocket-client-script`
    `kotlin-project-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
}