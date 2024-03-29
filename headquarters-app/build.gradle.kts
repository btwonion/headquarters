plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `project-client-script`
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = BuildConstants.ktorVersion
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("org.rauschig:jarchivelib:1.2.0")
}

kotlin {
    sourceSets.all {
        languageSettings {
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}