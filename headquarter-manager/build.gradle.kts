plugins {
    kotlin("jvm")
    application
    `kotlin-project-script`
    `mongo-script`
    `realms-script`
    `websocket-client-script`
    `terminal-script`
}

dependencies {
    implementation(project(":${BuildConstants.projectName}-api"))
}

application.mainClass.set("dev.nyon.headquarter.manager.ManagerRunnerKt")