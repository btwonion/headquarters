package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.database.updateProfile
import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.modrinthConnector
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.connector.modrinth.models.project.DependencyType
import dev.nyon.headquarters.connector.modrinth.models.project.version.File
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.requests.getVersion
import dev.nyon.headquarters.connector.modrinth.requests.listVersions
import io.ktor.http.*
import kotlin.io.path.deleteIfExists

suspend fun Profile.assureModExists(project: Project) {
    val version = modrinthConnector.getVersion(project.versionID) ?: return
    val files = mutableListOf<File>()
    val modsDir = profileDir.resolve("mods/")
    files.add(version.files.first { it.primary })
    version.dependencies?.forEach { dependency ->
        if (dependency.dependencyType != DependencyType.Required) return@forEach
        if (mods.any { mod -> mod.versionID == dependency.versionID }) return@forEach
        val matchingProject = mods.find { mod -> mod.projectID == dependency.projectID }
        val dependencyVersion = if (dependency.versionID == null) modrinthConnector.listVersions(
            dependency.projectID!!,
            mutableListOf(loader).also { if (loader == Loader.Quilt) it.add(Loader.Fabric) },
            eventuallySupportedVersions()
        )!!.minByOrNull { it.published }!! else modrinthConnector.getVersion(dependency.versionID!!)!!
        if (matchingProject != null) {
            val matchingProjectVersion = modrinthConnector.getVersion(matchingProject.versionID)!!
            if (matchingProjectVersion.published > dependencyVersion.published) return@forEach
            updateProfile(profileID) {
                this.mods.first { it.versionID == matchingProject.versionID }.versionID = dependencyVersion.id
            }
            modsDir.resolve(matchingProjectVersion.files.first { it.primary }.fileName).deleteIfExists()
        }
        files.add(dependencyVersion.files.first { it.primary })
        if (matchingProject == null) updateProfile(profileID) {
            this.mods.add(Project(versionID = dependencyVersion.id, projectID = dependencyVersion.projectID))
        }
    }
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