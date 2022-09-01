package dev.nyon.headquarter.api.networking

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.distribution.Client
import dev.nyon.headquarter.api.player.NetworkPlayer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class NetworkMessage

@Serializable
data class PlayerRequest(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

@Serializable
data class PlayerRequestAnswer(val player: NetworkPlayer?) : NetworkMessage()

@Serializable
data class PlayerCreate(val player: NetworkPlayer) : NetworkMessage()

@Serializable
data class PlayerDelete(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

@Serializable
data class PlayerUpdate(val player: NetworkPlayer) : NetworkMessage()

@Serializable
data class ServiceRequest(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

@Serializable
data class ServiceRequestAnswer(val service: Client?) : NetworkMessage()

@Serializable
data class ServiceCreate(val service: Client) : NetworkMessage()

@Serializable
data class ServiceDelete(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

@Serializable
data class ServiceUpdate(val service: Client) : NetworkMessage()