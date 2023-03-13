package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.loader.FabricCreateProcess
import dev.nyon.headquarters.app.loader.VanillaCreateProcess
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import io.ktor.http.*
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.createDirectories


fun Profile.init() {
    appScope.launch {
        realm.write {
            copyToRealm(this@init)
        }

        val librariesDir = profileDir.resolve("libraries/").createDirectories()
        val modsDir = profileDir.resolve("mods/").createDirectories()
        val resourcePackDir = profileDir.resolve("resourcepacks/").createDirectories()
        profileDir.resolve("versions/").createDirectories()
        profileDir.resolve("assets/").createDirectories()

        downloadMojangLibraries(librariesDir)
        downloadLauncherLibraries()

        modsDir.downloadProjects(mods)
        resourcePackDir.downloadProjects(resourcePacks)
    }
}

private suspend fun Path.downloadProjects(projects: List<Project>) {
    val versions = modrinthConnector.getVersions(projects.map { it.versionID })
        ?: error("Couldn't request versions for artifacts: '${projects.joinToString { it.versionID }}'")

    versions.map { it.files }.flatten().forEach {
        ktorClient.downloadFile(
            Url(it.url), this@downloadProjects.resolve(it.fileName)
        )
    }
}

private suspend fun Profile.downloadMojangLibraries(librariesPath: Path) {
    val meta = mojangConnector.getVersionPackage(minecraftVersion.id)
        ?: error("Couldn't find minecraft version '$minecraftVersion.id'")
    val libraries =
        meta.libraries.filter { it.rules == null || it.rules!!.all { rule -> rule.os == null } || it.rules!!.any { rule -> rule.os!!.name == os } }
    libraries.forEach { library ->
        librariesPath.resolve(library.downloads.artifact.path!!.dropLastWhile { it != '/' }).createDirectories()
        ktorClient.downloadFile(
            Url(library.downloads.artifact.url), librariesPath.resolve(library.downloads.artifact.path!!)
        )
    }
}

private suspend fun Profile.downloadLauncherLibraries() {
    when (loader) {
        Loader.Fabric -> FabricCreateProcess(profileDir, this.minecraftVersion, loaderProfile).installLibraries()
        else -> VanillaCreateProcess(profileDir, this.minecraftVersion).installLibraries()
    }
}