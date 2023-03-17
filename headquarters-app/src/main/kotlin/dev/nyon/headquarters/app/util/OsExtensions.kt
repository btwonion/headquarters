package dev.nyon.headquarters.app.util

import dev.nyon.headquarters.connector.mojang.models.`package`.Os

val Os.commonFileEnding: String
    get() = when(this) {
        Os.Windows -> ".zip"
        Os.Linux -> ".tar.gz"
        Os.OsX -> ".pkg"
    }