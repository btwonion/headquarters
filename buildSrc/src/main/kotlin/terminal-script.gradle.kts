plugins{
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies{
    implementation("com.github.ajalt:mordant:1.2.1")
    implementation("com.github.ajalt.clikt:clikt:3.5.0")
}