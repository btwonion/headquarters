package dev.nyon.headquarters.app.user

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, val sharedProfiles: List<String>, val profiles: List<String>)