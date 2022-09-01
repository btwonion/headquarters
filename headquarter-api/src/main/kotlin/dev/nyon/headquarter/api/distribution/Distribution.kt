package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.InternalHeadquarterAPI
import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.networking.Host
import kotlinx.serialization.Serializable
import java.util.*

@InternalHeadquarterAPI
interface Distribution {

    val uuid: @Serializable(with = UUIDSerializer::class) UUID
    val host: Host
    val availableMemory: Int

}