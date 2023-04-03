package dev.nyon.headquarters.app.database.models

import dev.nyon.headquarters.app.database.DatabaseEntry
import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(override val version: Int = 1, var whiteTheme: Boolean = false) : DatabaseEntry<Unit> {
    override fun migrateToNewerEntry() {}
}