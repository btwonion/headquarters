package dev.nyon.headquarters.app.profile.local

import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.profile.models.LocalProfile
import dev.nyon.headquarters.app.profile.models.Profile
import dev.nyon.headquarters.app.profile.models.ProjectEntry
import dev.nyon.headquarters.connector.fabric.models.LauncherMeta
import dev.nyon.headquarters.connector.fabric.requests.getLoaderOfGameAndLoaderVersion
import dev.nyon.headquarters.connector.fabric.requests.getLoaderVersions
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import dev.nyon.headquarters.connector.mojang.models.`package`.Os
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

val testModProfile = Profile.ModProfile(
    "1.19.2", Loader.Fabric, listOf(), listOf(), listOf(ProjectEntry("lg17V3i3", "qak00xay", true)), "sadawa"
)
val testResourcePackProfile =
    Profile.ResourcePackProfile("1.19.2", listOf(ProjectEntry("w0TnApzs", "flpvzFNA", true)), "asdaw")

suspend fun createProfile(profile: LocalProfile) {
    realm.write {
        copyToRealm(profile)
    }

    val profileDir = runningDir.resolve("profiles/${profile.name}/").createDirectories()
    val librariesDir = profileDir.resolve("libraries/").createDirectories()
    val configDir = profileDir.resolve("config/").createDirectories()
    val modsDir = profileDir.resolve("mods/").createDirectories()
    val resourcePackDir = profileDir.resolve("resourcepacks/").createDirectories()

    profile.downloadLauncherLibraries(librariesDir)
    profile.downloadMojangLibraries(librariesDir)

    profile.downloadProjects(modsDir, resourcePackDir)

    profile.writeConfigs(configDir)
}

private suspend fun Path.downloadProjects(projects: List<Version>) {
    projects.forEach {
        ktorClient.downloadFile(
            Url(it.files[0].url), this@downloadProjects.resolve(it.files[0].fileName)
        )
    }
}

private suspend fun LocalProfile.downloadMojangLibraries(libariesPath: Path) {
    val meta = mojangConnector.getVersionPackage(testModProfile.gameVersion)
        ?: error("cannot find version package of ${testModProfile.gameVersion}")
    val libraries =
        meta.libraries.filter { it.rules == null || it.rules!!.all { rule -> rule.os == null } || it.rules!!.any { rule -> rule.os!!.name == Os.Linux } }
    libraries.forEach {
        ktorClient.downloadFile(
            Url(it.downloads.artifact.url), libariesPath.resolve(it.downloads.artifact.url.split("/").last())
        )
    }
}

private suspend fun LocalProfile.downloadLauncherLibraries(librariesPath: Path) {
    if (testModProfile.loader == Loader.Fabric) {
        val loaderVersion = fabricConnector.getLoaderOfGameAndLoaderVersion(
            testModProfile.gameVersion, fabricConnector.getLoaderVersions()!![0].version
        ) ?: error("loader cannot be found")
        (loaderVersion.launcherMeta as LauncherMeta).libraries.common.forEach { artifact ->
            val split = artifact.name.split(":")
            val fileName = "${split[1]}-${split[2]}.jar"
            val url = "${artifact.url}${split[0].replace(".", "/")}${
                split.toMutableList().also { it.removeFirst() }.joinToString("/", prefix = "/", postfix = "/")
            }$fileName"
            ktorClient.downloadFile(Url(url), librariesPath.resolve(fileName))
        }
    }
}

private suspend fun LocalProfile.downloadProjects(modsPath: Path, resourcePackPath: Path) {
    modrinthConnector.getVersions(testModProfile.mods.filter { it.enabled }.map { it.versionID }).let {
        if (it == null) return@let
        modsPath.downloadProjects(it)
    }

    modrinthConnector.getVersions(testResourcePackProfile.resourcePacks.filter { it.enabled }.map { it.versionID }).let {
        if (it == null) return@let
        resourcePackPath.downloadProjects(it)
    }
}

private suspend fun LocalProfile.writeConfigs(configPath: Path) {
    testModProfile.configs.forEach {
        withContext(Dispatchers.IO) {
            configPath.resolve(if (it.directory != "") "${it.directory}/${it.name}" else it.name).createDirectories()
                .writeBytes(it.content)
        }
    }
}