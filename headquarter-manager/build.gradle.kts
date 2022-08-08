plugins {
    kotlin("jvm")
    application
    `kotlin-project-script`
    `mongo-script`
    `realms-script`
    `websocket-client-script`
    `websocket-server-script`
    `docker-script`
    `terminal-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
}