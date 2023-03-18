package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.loader.FabricCreateProcess
import dev.nyon.headquarters.app.loader.VanillaCreateProcess
import dev.nyon.headquarters.app.util.commonFileEnding
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.app.util.requestReleases
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.rauschig.jarchivelib.ArchiverFactory
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.notExists


suspend fun Profile.init() {
    val modsDir = profileDir.resolve("mods/").createDirectories()
    val resourcePackDir = profileDir.resolve("resourcepacks/").createDirectories()
    profileDir.resolve("versions/").createDirectories()

    downloadMojangLibraries()
    downloadLauncherLibraries()

    modsDir.downloadProjects(mods)
    resourcePackDir.downloadProjects(resourcePacks)

    val release =
        requestReleases("adoptium/temurin17-binaries").first { it.name.startsWith("jdk-${minecraftVersion.javaVersion.majorVersion}") }
    val asset = release.assets.find { asset ->
        val name = asset.name.dropWhile { it != '-' }.drop(1)
        name.startsWith("jdk_${arch}_${os.name.lowercase()}_hotspot_") && name.endsWith(os.commonFileEnding)
    } ?: error("cannot find java package matching your system")
    val filePath = javaVersionsDir.resolve("java_${minecraftVersion.javaVersion.majorVersion}${os.commonFileEnding}")
    ktorClient.downloadFile(Url(asset.browser_download_url), filePath)
    ArchiverFactory.createArchiver(filePath.toFile()).extract(
        filePath.toFile(),
        javaVersionsDir.resolve("java_${minecraftVersion.javaVersion.majorVersion}").toFile()
    )

    val assetIndex = assetsDir.resolve("indexes/${minecraftVersion.assetIndex.id}.json")
    if (assetIndex.notExists()) {
        ktorClient.downloadFile(
            Url(
                minecraftVersion.assetIndex.url
            ), assetIndex
        )
        downloadAssets()
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

private suspend fun Profile.downloadMojangLibraries() {
    val libraries =
        minecraftVersion.libraries.filter {
            if (it.rules == null) return@filter true
            if (it.rules!!.any { rule -> rule.os?.name == os }) return@filter true
            false
        }
    libraries.forEach { library ->
        val dirPath = librariesDir.resolve(library.downloads.artifact.path!!.dropLastWhile { it != '/' })
        val path = librariesDir.resolve(library.downloads.artifact.path!!)
        if (path.exists()) return@forEach
        dirPath.createDirectories()
        ktorClient.downloadFile(Url(library.downloads.artifact.url), path)
    }

    ktorClient.downloadFile(Url(minecraftVersion.downloads.client.url), profileDir.resolve("client.jar"))
}

private suspend fun Profile.downloadLauncherLibraries() {
    when (loader) {
        Loader.Fabric -> FabricCreateProcess(profileDir, this.minecraftVersion, loaderProfile).installLibraries()
        else -> VanillaCreateProcess(profileDir, this.minecraftVersion).installLibraries()
    }
}

private suspend fun Profile.downloadAssets() {
    val objectDir = assetsDir.resolve("objects/")
    val assetString = ktorClient.get(minecraftVersion.assetIndex.url).bodyAsText()
    val assetObject = Json.parseToJsonElement(assetString).jsonObject["objects"]
    assetObject?.jsonObject?.values?.map { it.jsonObject }?.forEach {
        val hash = it["hash"]?.jsonPrimitive?.content
        val filePath = objectDir.resolve("${hash?.take(2)}/$hash")
        if (filePath.exists()) return@forEach
        filePath.parent.createDirectories()
        ktorClient.downloadFile(
            Url("https://dl.hglabor.de/mirror/assets/1.19.4/objects/${hash?.take(2)}/$hash"), filePath
        )
    }
}