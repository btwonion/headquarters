plugins {
    kotlin("jvm")
    `kotlin-project-script`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.nyon:headquarters-connector-modrinth:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-core:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-fabric:${BuildConstants.connectorVersion}")
    implementation("dev.nyon:headquarters-connector-mojang:${BuildConstants.connectorVersion}")
}

publishing {
    repositories {
        maven {
            name = "nyon"
            url = uri("https://repo.nyon.dev/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.nyon"
            artifactId = "headquarters-api"
            version = "1.0.0"
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}