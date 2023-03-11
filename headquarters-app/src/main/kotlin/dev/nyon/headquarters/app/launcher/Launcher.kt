package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun main() {
    realm.query<Profile>().find()[0].launch()
}

suspend fun Profile.launch() {
    val startArgs = buildList<String> {
        add("java")
        addVanillaArguments(this@launch)
        addFabricArguments(this@launch)

        add("-Dminecraft.launcher.brand=headquarters")
        add("-Dminecraft.launcher.version=1.0.0")
    }

    withContext(Dispatchers.IO) {
        ProcessBuilder().command(startArgs).start()
    }
}