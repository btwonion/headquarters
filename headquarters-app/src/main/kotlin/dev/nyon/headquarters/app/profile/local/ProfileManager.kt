package dev.nyon.headquarters.app.profile.local

import dev.nyon.headquarters.app.connector
import dev.nyon.headquarters.app.downloadFile
import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.profile.models.LocalProfile
import dev.nyon.headquarters.app.profile.models.Profile
import dev.nyon.headquarters.app.profile.models.ProjectEntry
import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import io.ktor.http.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

val testModProfile = Profile.ModProfile(
    "1.19.2", Loader.Fabric, listOf(), listOf(), listOf(ProjectEntry("lg17V3i3", "qak00xay")), "sadawa"
)

suspend fun createProfile(profile: LocalProfile) {
    val profileDir = Path("${runningDir.toAbsolutePath()}/${profile.name}/").createDirectories()

    val modVersions = connector.getVersions(testModProfile.mods.map { it.versionID }) ?: error("asd")
    val modPath = profileDir.resolveSibling("/mods/")
    if (!modPath.exists()) modPath.createDirectories()
    modPath.downloadProjects(modVersions.map { Url(it.files[0].url) })
}

private suspend fun Path.downloadProjects(projects: List<Url>) {
    projects.forEach {
        ktorClient.downloadFile(it, this@downloadProjects)
    }
}