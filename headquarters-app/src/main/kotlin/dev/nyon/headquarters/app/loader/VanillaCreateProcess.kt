package dev.nyon.headquarters.app.loader

import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import java.nio.file.Path

class VanillaCreateProcess(override val profileDir: Path, override val minecraftVersion: MinecraftVersion) :
    LoaderCreateProcess {
    override suspend fun installLibraries() {}
}