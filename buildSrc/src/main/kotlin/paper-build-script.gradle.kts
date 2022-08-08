plugins {
    kotlin("jvm")
    id("net.minecrell.plugin-yml.bukkit")
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    library(kotlin("stdlib"))
}

/*tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "description" to project.description
        )

        inputs.properties(props)

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

 */