package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.api.Profile
import dev.nyon.headquarters.api.ProjectEntry
import dev.nyon.headquarters.api.Visibility
import dev.nyon.headquarters.api.mods
import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.connector.fabric.models.LauncherMeta
import dev.nyon.headquarters.connector.fabric.requests.getLoaderOfGameAndLoaderVersion
import dev.nyon.headquarters.connector.fabric.requests.getLoaderVersions
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import dev.nyon.headquarters.connector.mojang.models.`package`.Os
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeBytes

val testModProfile = Profile.ModProfile(
    "sadawa",
    "btwonion",
    MinecraftVersion(
        "1.19.3",
        MinecraftVersionType.Release,
        "https://piston-meta.mojang.com/v1/packages/6607feafdb2f96baad9314f207277730421a8e76/1.19.3.json",
        Instant.parse("2022-09-13T14:29:56+00:00"),
        Instant.parse("2022-09-13T14:29:56+00:00"),
        "6607feafdb2f96baad9314f207277730421a8e76",
        1
    ),
    Loader.Fabric,
    listOf(),
    listOf(),
    listOf(ProjectEntry("lg17V3i3", "7dVSeEw5", true)),
    listOf(),
    0,
    0,
    listOf(),
    Visibility.Discoverable,
    "test"
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
    profileDir.resolve("assets/").createDirectories()

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
    val meta = mojangConnector.getVersionPackage(testModProfile.id)
        ?: error("cannot find version package of ${testModProfile.id}")
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
            testModProfile.id, fabricConnector.getLoaderVersions()!![0].version
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

    modrinthConnector.getVersions(testResourcePackProfile.resourcePacks.filter { it.enabled }.map { it.versionID })
        .let {
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