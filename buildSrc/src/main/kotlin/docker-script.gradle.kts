plugins{
    kotlin("jvm")
}

repositories{
    mavenCentral()
}

dependencies {
    val dockerVersion = "3.2.13"
    implementation("com.github.docker-java:docker-java-transport-httpclient5:$dockerVersion")
    implementation("com.github.docker-java:docker-java-core:$dockerVersion")
}