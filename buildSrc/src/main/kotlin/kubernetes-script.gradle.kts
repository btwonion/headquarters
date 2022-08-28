plugins{
    kotlin("jvm")
}

repositories{
    mavenCentral()
}

dependencies {
    implementation("io.kubernetes:client-java:${BuildConstants.kubernetesVersion}")
}