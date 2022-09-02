package dev.nyon.headquarter.api.networking

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.distribution.Client
import dev.nyon.headquarter.api.player.NetworkPlayer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class NetworkMessage

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