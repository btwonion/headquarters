package dev.nyon.headquarters.api.user

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, val profiles: List<String>)