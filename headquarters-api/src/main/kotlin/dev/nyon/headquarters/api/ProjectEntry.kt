package dev.nyon.headquarters.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectEntry(val id: String, @SerialName("version_id") val versionID: String, val enabled: Boolean)