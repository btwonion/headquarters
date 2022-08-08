plugins {
    kotlin("jvm")
    `websocket-client-script`
    `kotlin-project-script`
    `http-client-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
}