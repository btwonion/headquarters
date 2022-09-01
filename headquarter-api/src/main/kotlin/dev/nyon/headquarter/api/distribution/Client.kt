package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.networking.Host
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Client(
    override val uuid: @Serializable(with = UUIDSerializer::class) UUID,
    val displayName: String,
    override val host: Host,
    override val availableMemory: Int,
    val clientType: ClientType
) : Distribution, RealmObject {}

@Serializable
enum class ClientType {

    Paper, Fabric, Velocity

}