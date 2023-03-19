package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.modrinthConnector
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.connector.modrinth.models.project.version.File
import dev.nyon.headquarters.connector.modrinth.requests.getVersion
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import io.ktor.http.*

suspend fun Profile.assureModExists(project: Project) {
    val version = modrinthConnector.getVersion(project.versionID) ?: return
    val files = mutableListOf<File>()
    files.addAll(version.files)
    if (version.dependencies != null) files.addAll(
        modrinthConnector.getVersions(version.dependencies!!.map { it.versionID!! })!!
            .map { it.files }.flatten()
    )
    files.forEach {
        ktorClient.downloadFile(Url(it.url), profileDir.resolve("mods/${it.fileName}"))
    }
}

suspend fun Profile.assureTexturePackExists(project: Project) {
    val version = modrinthConnector.getVersion(project.versionID) ?: return
    version.files.forEach {
        ktorClient.downloadFile(Url(it.url), profileDir.resolve("mods/${it.fileName}"))
    }
}