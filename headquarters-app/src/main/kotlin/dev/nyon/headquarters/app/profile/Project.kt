package dev.nyon.headquarters.app.profile

import kotlinx.serialization.Serializable

@Serializable
data class Project(var versionID: String, var projectID: String, var enabled: Boolean = true)