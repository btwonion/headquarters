package dev.nyon.headquarters.app.launcher.auth

import dev.nyon.headquarters.app.util.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MinecraftAccountInfo(
    val uuid: @Serializable(with = UUIDSerializer::class) UUID,
    val accessToken: String,
    val expireDate: Instant,
    val username: String,
    val uhs: String,
    var current: Boolean = false
)