package dev.nyon.headquarters.api.user

import dev.nyon.headquarters.api.IdHolder
import kotlinx.serialization.Serializable

@Serializable
data class User(override val id: String, val githubID: String, val profiles: List<String>) : IdHolder