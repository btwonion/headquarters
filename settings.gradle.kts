val projectName = "headquarter"
rootProject.name = projectName

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.10"
    }
}

include(":$projectName-api:internalMain")
findProject(":$projectName-api:internalMain")?.name = "$projectName-internalApi"