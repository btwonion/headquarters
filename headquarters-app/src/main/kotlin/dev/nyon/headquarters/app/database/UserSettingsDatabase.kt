package dev.nyon.headquarters.app.database

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.database.models.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

val userSettingsDB = Database<UserSettings>("user_settings", mutableMapOf(1 to UserSettings::class))
val userSettings = MutableStateFlow(UserSettings())

private val saveMutex = Mutex()

@Suppress("unused")
private val updater = appScope.launch {
    userSettings.update { userSettingsDB.loadEntries<UserSettings>().firstOrNull() ?: UserSettings() }
    userSettings.collect {
        saveMutex.withLock {
            delay(500.milliseconds)
            userSettingsDB.saveEntries(listOf(it))
        }
    }
}

inline fun updateUserSettings(crossinline transformation: UserSettings.() -> Unit) =
    appScope.launch { userSettings.update { foundSetting -> foundSetting.copy().apply(transformation) } }