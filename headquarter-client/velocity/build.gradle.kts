plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
    implementation(project(":$projectName-client:$projectName-client-common"))
}