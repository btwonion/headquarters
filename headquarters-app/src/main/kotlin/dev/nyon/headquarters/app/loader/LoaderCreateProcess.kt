package dev.nyon.headquarters.app.loader

import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import java.nio.file.Path

sealed interface LoaderCreateProcess {
    val profileDir: Path
    val minecraftVersion: MinecraftVersion
    suspend fun installLibraries()
}