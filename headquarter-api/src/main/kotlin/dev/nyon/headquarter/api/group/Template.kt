package dev.nyon.headquarter.api.group

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.networking.Host
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Template(
    val uuid: @Serializable(with = UUIDSerializer::class) UUID, var name: String, var host: Host, var path: String, var extraPorts: List<Int>
)