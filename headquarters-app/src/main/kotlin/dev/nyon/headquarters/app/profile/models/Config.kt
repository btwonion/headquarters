package dev.nyon.headquarters.app.profile.models

import kotlinx.serialization.Serializable

@Serializable
data class Config(val directory: String = "", val content: ByteArray, val name: String)