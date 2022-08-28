package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Client(
    val uuiD: @Serializable(with = UUIDSerializer::class) UUID,
    val hosT: String,
    val availableMemorY: Int,
    val usedMemorY: Int,
    val clientType: ClientType
) : Distribution(
    uuiD, hosT, availableMemorY, usedMemorY
) {}

@Serializable
enum class ClientType {

    Paper, Fabric, Velocity

}