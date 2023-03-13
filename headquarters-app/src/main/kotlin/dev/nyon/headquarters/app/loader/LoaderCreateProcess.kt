package dev.nyon.headquarters.app.loader

import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import java.nio.file.Path

sealed interface LoaderCreateProcess {
    val profileDir: Path
    val minecraftVersion: VersionPackage
    suspend fun installLibraries()
}