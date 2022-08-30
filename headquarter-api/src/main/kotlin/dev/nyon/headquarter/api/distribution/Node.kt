package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.networking.Host
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Node(
    val uuiD: @Serializable(with = UUIDSerializer::class) UUID,
    val hosT: Host,
    val availableMemorY: Int
) : Distribution(
    uuiD, hosT, availableMemorY
), RealmObject {}