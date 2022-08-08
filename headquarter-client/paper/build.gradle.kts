plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `paper-build-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
    implementation(project(":$projectName-client:$projectName-client-common"))
}