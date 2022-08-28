import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${BuildConstants.coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${BuildConstants.serializationVersion}")
}

tasks {
    withType<JavaCompile> {
        options.apply {
            release.set(18)
            encoding = "UTF-8"
        }
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "18"
            listOf("InternalHeadquarterAPI", "ExperimentalHeadquarterApi").forEach {
                freeCompilerArgs += "dev.nyon.headquarter.api.common.$it"
            }
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}