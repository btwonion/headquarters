package dev.nyon.headquarters.app.user

import dev.nyon.headquarters.app.appScope
import kotlinx.coroutines.launch

fun createDefaultUserSettings(): UserSettings {
    val settings = UserSettings()
    appScope.launch {
        userSettingDB.write { copyToRealm(settings) }
    }
    return settings
}