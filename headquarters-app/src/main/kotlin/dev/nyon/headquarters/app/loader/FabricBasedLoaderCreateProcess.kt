package dev.nyon.headquarters.app.loader

import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.librariesDir
import dev.nyon.headquarters.app.util.downloadFile
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import io.ktor.http.*
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

class FabricBasedLoaderCreateProcess(
    override val profileDir: Path,
    override val minecraftVersion: VersionPackage,
    private val loaderProfile: LoaderProfile
) : LoaderCreateProcess {

    override suspend fun installLibraries() {
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