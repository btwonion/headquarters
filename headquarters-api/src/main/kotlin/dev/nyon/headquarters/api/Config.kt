package dev.nyon.headquarters.api

import kotlinx.serialization.Serializable

@Serializable
data class Config(var directory: String = "", var content: ByteArray, var name: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Config

        if (directory != other.directory) return false
        if (!content.contentEquals(other.content)) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = directory.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}