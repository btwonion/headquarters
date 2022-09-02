package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.networking.Host
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class Client : RealmObject {

    var uuid: @Serializable(with = UUIDSerializer::class) UUID = UUID.randomUUID()
    var displayName: String = ""
    var host: Host = Host("0.0.0.0", null)
    var availableMemory: Int = 0
    var clientType: ClientType = ClientType.Fabric

}

@Serializable
enum class ClientType {

    Paper, Fabric, Velocity

}