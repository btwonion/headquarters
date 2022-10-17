package dev.nyon.headquarters.app.profile.local

import dev.nyon.headquarters.app.connector
import dev.nyon.headquarters.app.downloadFile
import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.profile.models.LocalProfile
import dev.nyon.headquarters.app.profile.models.Profile
import dev.nyon.headquarters.app.profile.models.ProjectEntry
import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

val testModProfile = Profile.ModProfile(
    "1.19.2", Loader.Fabric, listOf(), listOf(), listOf(ProjectEntry("lg17V3i3", "qak00xay", true)), "sadawa"
)

suspend fun createProfile(profile: LocalProfile) {
    realm.write {
        copyToRealm(profile)
    }
    val profileDir = Path("${runningDir.toAbsolutePath()}/profiles/${profile.name}/").createDirectories()

    connector.getVersions(testModProfile.mods.map { it.versionID }).let {
        if (it == null) return@let
        Path("${profileDir.toAbsolutePath()}/mods/").createDirectories().downloadProjects(it)
    }

    testModProfile.configs.forEach {
        withContext(Dispatchers.IO) {
            Path("${profileDir.toAbsolutePath()}/config/${if (it.directory != "") "${it.directory}/${it.name}" else it.name}").createDirectories()
                .writeBytes(it.content)
        }
    }
}

private suspend fun Path.downloadProjects(projects: List<Version>) {
    projects.forEach {
        ktorClient.downloadFile(
            Url(it.files[0].url), Path("${this@downloadProjects.toAbsolutePath()}/${it.files[0].fileName}")
        )
    }
}