package dev.nyon.headquarters.app.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

object OldUUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("old_uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        val uuidWithoutHyphens = decoder.decodeString()
        return UUID.fromString(
            uuidWithoutHyphens.substring(0, 8) + "-" +
                    uuidWithoutHyphens.substring(8, 12) + "-" +
                    uuidWithoutHyphens.substring(12, 16) + "-" +
                    uuidWithoutHyphens.substring(16, 20) + "-" +
                    uuidWithoutHyphens.substring(20)
        )
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString().replace("-", ""))
    }

}