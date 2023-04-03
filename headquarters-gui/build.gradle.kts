plugins {
    kotlin("jvm")
    application
    `compose-script`
    `kotlin-project-script`
    `project-client-script`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject.project(":headquarters-app"))
}

application {
    mainClass.set("dev.nyon.headquarters.gui.ApplicationKt")
}