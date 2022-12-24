package dev.nyon.headquarters.app.loader

import java.nio.file.Path

sealed interface LoaderCreateProcess {
    val profileDir: Path
    suspend fun createDirectories()
}