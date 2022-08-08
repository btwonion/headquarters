val projectName = "headquarter"
rootProject.name = projectName

pluginManagement {
    plugins {
        kotlin("jvm") version "1.7.10"
    }
}

fun includeWithName(path: String, name: String) {
    include(path)
    findProject(path)?.name = name
}

includeWithName(":$projectName-api:internalMain", "$projectName-internalApi")
includeWithName(":$projectName-api:publicMain", "$projectName-publicApi")