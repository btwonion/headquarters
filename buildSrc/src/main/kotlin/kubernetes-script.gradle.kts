plugins{
    kotlin("jvm")
}

repositories{
    mavenCentral()
}

dependencies {
    implementation("io.fabric8:kubernetes-client:${BuildConstants.kubernetesVersion}")
}