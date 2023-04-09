package dev.nyon.headquarters.app.database

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.profile.createDefaultProfile
import dev.nyon.headquarters.app.profile.init
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

val profileDB = Database<Profile>("profiles", mutableMapOf(1 to Profile::class))
val profilesTrigger = MutableStateFlow(false)
val profiles = mutableListOf<Profile>()

private var saveActionInProgress = false

@Suppress("unused")
private val updater = appScope.launch {
    profiles.clear()
    profiles.addAll(profileDB.loadEntries<Profile>())
    if (profiles.isEmpty()) profiles.add(createDefaultProfile().apply new@{ launch { this@new.initLoaderProfile(); this@new.initMinecraftVersionPackage(); this@new.init() } })
    launch { profiles.forEach { profile -> profile.initLoaderProfile(); profile.initMinecraftVersionPackage() } }
    profilesTrigger.update { !it }

    profilesTrigger.collect {
        if (saveActionInProgress) return@collect
        launch {
            saveActionInProgress = true
            delay(500.milliseconds)
            profileDB.saveEntries(profiles)
            saveActionInProgress = false
        }
    }
}

inline fun updateProfile(id: String, crossinline transformation: Profile.() -> Unit) =
    appScope.launch {
        profiles.first { profile -> profile.profileID == id }
            .apply(transformation); profilesTrigger.update { !it }
    }