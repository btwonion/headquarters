package dev.nyon.headquarters.app.util

import dev.nyon.headquarters.connector.mojang.models.`package`.Os
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType

val Os.commonArchiver: Archiver
    get() = when(this) {
        Os.Windows -> ArchiverFactory.createArchiver(ArchiveFormat.ZIP)
        Os.OsX, Os.Linux -> ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
    }

val Os.commonFileEnding: String
    get() = when(this) {
        Os.Windows -> ".zip"
        Os.Linux -> ".tar.gz"
        Os.OsX -> ".tar.gz"
    }