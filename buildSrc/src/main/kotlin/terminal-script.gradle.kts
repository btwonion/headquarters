plugins{
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies{
    implementation("com.github.ajalt.mordant:mordant:${BuildConstants.mordantVersion}")
    implementation("com.github.ajalt.clikt:clikt:${BuildConstants.cliktVersion}")
}