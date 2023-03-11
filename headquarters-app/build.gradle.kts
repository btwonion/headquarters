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
    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("me.nullicorn:ms-to-mca:0.0.1")
}