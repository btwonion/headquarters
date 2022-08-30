package dev.nyon.headquarter.api.networking

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.distribution.Node
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class NetworkMessage

@Serializable
data class NodeRequest(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

@Serializable
data class NodeRequestAnswer(val node: Node?) : NetworkMessage()

@Serializable
data class NodeCreate(val node: Node) : NetworkMessage()

@Serializable
data class NodeDelete(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

@Serializable
data class NodeUpdate(val node: Node) : NetworkMessage()