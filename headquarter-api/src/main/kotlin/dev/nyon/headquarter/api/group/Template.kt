package dev.nyon.headquarter.api.group

import dev.nyon.headquarter.api.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Template(val uuid: @Serializable(with = UUIDSerializer::class) UUID, val name: String, val groupId: @Serializable(with = UUIDSerializer::class) UUID)