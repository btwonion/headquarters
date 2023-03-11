plugins {
    kotlin("jvm")
    application
    `compose-script`
    `kotlin-project-script`
    `project-client-script`
    `realm-script`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject.project(":headquarters-app"))
    implementation("me.obsilabor:piston-meta-kt:1.0.6")
}

application {
    mainClass.set("dev.nyon.headquarters.gui.ApplicationKt")
}