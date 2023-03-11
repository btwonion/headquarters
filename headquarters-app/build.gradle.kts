plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `project-client-script`
    `realm-script`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.obsilabor:piston-meta-kt:1.0.6")
}