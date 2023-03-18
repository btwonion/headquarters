package dev.nyon.headquarters.app.loader

import dev.nyon.headquarters.app.fabricConnector
import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.librariesDir
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.fabric.requests.getLoadersOfGameVersion
import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

class FabricCreateProcess(
    override val profileDir: Path,
    override val minecraftVersion: VersionPackage,
    private val loaderProfile: LoaderProfile
) : LoaderCreateProcess {

    override suspend fun installLibraries() {
        val loaderVersion = (fabricConnector.getLoadersOfGameVersion(
            minecraftVersion.id
        )?.first()
            ?: error("Cannot find compatible fabric loader for version '$minecraftVersion.id'")).loader.version

        val versionsDir = profileDir.resolve("versions/")
        val zipPath = versionsDir.resolve("${loaderProfile.id}.zip")
        ktorClient.downloadFile(
            Url("https://meta.fabricmc.net/v2/versions/loader/${minecraftVersion.id}/$loaderVersion/profile/zip"),
            zipPath
        )
        withContext(Dispatchers.IO) {
            val zipArchiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP)
            zipArchiver.extract(zipPath.toFile(), versionsDir.toFile())
        }
        zipPath.deleteIfExists()

        loaderProfile.libraries.forEach { artifact ->
            val split = artifact.name.split(":")
            val fileName = "${split[1]}-${split[2]}.jar"
            val url = "${artifact.url}${split[0].replace(".", "/")}${
                split.drop(1).joinToString("/", prefix = "/", postfix = "/")
            }$fileName"

            val artifactPath =
                librariesDir.resolve("${split[0].replace(".", "/")}/${split[1]}/${split[2]}")
            if (artifactPath.exists()) return@forEach
            artifactPath.createDirectories()
            ktorClient.downloadFile(Url(url), artifactPath.resolve(fileName))
        }
    }
}