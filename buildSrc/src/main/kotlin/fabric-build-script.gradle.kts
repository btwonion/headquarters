plugins {
    kotlin("jvm")
    id("fabric-loom")
    id("io.github.juuxel.loom-quiltflower")
    id("org.quiltmc.quilt-mappings-on-loom")
}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://server.bbkr.space/artifactory/libs-release/")
    maven("https://maven.quiltmc.org/repository/release/")
}

dependencies {
    minecraft("com.mojang:minecraft:${BuildConstants.minecraftVersion}")
    mappings(loom.layered {
        addLayer(quiltMappings.mappings("org.quiltmc:quilt-mappings:${BuildConstants.quiltMappingsVersion}"))
        officialMojangMappings()
    })
    modImplementation("net.fabricmc:fabric-loader:${BuildConstants.fabricLoaderVersion}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${BuildConstants.fabricAPIVersion}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${BuildConstants.fabricLanguageKotlinVersion}")
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "description" to project.description,
            "mc_version" to "1.19.2"
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
}