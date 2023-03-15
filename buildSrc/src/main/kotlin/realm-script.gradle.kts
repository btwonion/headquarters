plugins {
    kotlin("jvm")
    id("io.realm.kotlin")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("io.realm.kotlin:library-base:${BuildConstants.realmVersion}")
}