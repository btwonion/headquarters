package dev.nyon.headquarters.gui.util

import androidx.compose.ui.graphics.Color
import dev.nyon.headquarters.connector.modrinth.models.project.version.VersionType

val VersionType.color
    get() = when (this) {
        VersionType.Alpha -> Color(0x99db3162)
        VersionType.Beta -> Color(0x99C26D1A)
        VersionType.Release -> Color(0x9924a54e)
    }