plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `velocity-build-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
    implementation(project(":$projectName-client:$projectName-client-common"))
}