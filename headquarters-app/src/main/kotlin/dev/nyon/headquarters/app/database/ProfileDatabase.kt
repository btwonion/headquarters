package dev.nyon.headquarters.app.database

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.profile.createDefaultProfile
import dev.nyon.headquarters.app.profile.init
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

val profileDB = Database<Profile>("profiles", mutableMapOf(1 to Profile::class))
val profiles = MutableStateFlow(mutableListOf<Profile>())

private val saveMutex = Mutex()

@Suppress("unused")
private val updater = appScope.launch {
    profiles.update {
        profileDB.loadEntries<Profile>().toMutableList().apply {
            if (this.isEmpty()) this.add(createDefaultProfile().apply new@{ launch { this@new.initLoaderProfile(); this@new.initMinecraftVersionPackage(); this@new.init() } })
            launch { this@apply.forEach { profile -> profile.initLoaderProfile(); profile.initMinecraftVersionPackage() } }
        }
    }

    profiles.collect {
        saveMutex.withLock {
            delay(500.milliseconds)
            profileDB.saveEntries(it)
        }
    }
}

inline fun updateProfile(id: String, crossinline transformation: Profile.() -> Unit) {
    appScope.launch {
        profiles.update { foundProfiles ->
            foundProfiles.apply { first { it.profileID == id }.apply(transformation) }
        }
    }
}