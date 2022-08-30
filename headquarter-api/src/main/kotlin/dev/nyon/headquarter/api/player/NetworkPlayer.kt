package dev.nyon.headquarter.api.player

import dev.nyon.headquarter.api.common.UUIDSerializer
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class NetworkPlayer(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : RealmObject