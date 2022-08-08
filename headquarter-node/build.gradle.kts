plugins {
    kotlin("jvm")
    `http-client-script`
    `http-server-script`
    `kotlin-project-script`
    `mongo-script`
    `realms-script`
    `websocket-client-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
}
