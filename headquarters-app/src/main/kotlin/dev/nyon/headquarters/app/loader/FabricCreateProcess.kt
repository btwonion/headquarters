package dev.nyon.headquarters.app.loader

import java.nio.file.Path
import kotlin.io.path.createDirectories

class FabricCreateProcess(override val profileDir: Path) : LoaderCreateProcess {
    override suspend fun createDirectories() {
        profileDir.resolve("mods/").createDirectories()
    }
}