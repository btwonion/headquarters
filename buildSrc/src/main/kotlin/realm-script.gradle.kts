plugins {
    kotlin("jvm")
    id("io.realm.kotlin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.realm.kotlin:library-base:${BuildConstants.realmVersion}")
}