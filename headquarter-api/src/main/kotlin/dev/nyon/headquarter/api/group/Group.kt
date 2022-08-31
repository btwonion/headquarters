package dev.nyon.headquarter.api.group

import dev.nyon.headquarter.api.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Group(
    val uuid: @Serializable(with = UUIDSerializer::class) UUID,
    var name: String,
    var description: String,
    var template: Template?,
    var static: Boolean,
    var maxMemory: Int,
    var maxRunningServices: Int,
    var minRunningServices: Int
)