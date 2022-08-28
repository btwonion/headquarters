package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.InternalHeadquarterAPI
import dev.nyon.headquarter.api.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@InternalHeadquarterAPI
@Serializable
sealed class Distribution(val uuid: @Serializable(with = UUIDSerializer::class) UUID) {

    abstract val host: String
    abstract val availableMemory: Int
    abstract val usedMemory: Int

}