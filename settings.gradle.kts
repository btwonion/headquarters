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

//Api
includeWithName(":$projectName-api:internalMain", "$projectName-internalApi")
includeWithName(":$projectName-api:publicMain", "$projectName-publicApi")

//Node
include(":$projectName-node")

//Manager
include(":$projectName-manager")

//Client
includeWithName(":$projectName-client:common", "$projectName-client-common")
includeWithName(":$projectName-client:paper", "$projectName-client-paper")
includeWithName(":$projectName-client:fabric", "$projectName-client-fabric")
includeWithName(":$projectName-client:velocity", "$projectName-client-velocity")