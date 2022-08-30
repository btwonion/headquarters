package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.networking.Host
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Client(
    val uuiD: @Serializable(with = UUIDSerializer::class) UUID,
    val hosT: Host,
    val availableMemorY: Int,
    val clientType: ClientType
) : Distribution(
    uuiD, hosT, availableMemorY
), RealmObject {}

@Serializable
enum class ClientType {

    Paper, Fabric, Velocity

}