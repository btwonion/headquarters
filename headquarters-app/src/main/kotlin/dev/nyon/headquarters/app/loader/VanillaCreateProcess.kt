package dev.nyon.headquarters.app.loader

import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import java.nio.file.Path

class VanillaCreateProcess(override val profileDir: Path, override val minecraftVersion: VersionPackage) :
    LoaderCreateProcess {
    override suspend fun installLibraries() {}
}