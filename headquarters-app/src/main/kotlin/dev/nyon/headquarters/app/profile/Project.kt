package dev.nyon.headquarters.app.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(val id: String, @SerialName("version_id") val versionID: String, val enabled: Boolean)