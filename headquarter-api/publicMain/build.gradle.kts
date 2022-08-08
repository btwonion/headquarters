plugins {
    kotlin("jvm")
    `kotlin-project-script`
}

dependencies {
    implementation(project(":$projectName-api:$projectName-internalApi"))
}