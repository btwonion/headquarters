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
    val ktorVersion = BuildConstants.ktorVersion
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}