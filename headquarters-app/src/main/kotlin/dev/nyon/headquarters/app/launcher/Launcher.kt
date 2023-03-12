package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.launcher.auth.MinecraftProfile
import dev.nyon.headquarters.app.launcher.auth.MinecraftCredentials
import dev.nyon.headquarters.app.launcher.auth.XBoxAuthResponse
import dev.nyon.headquarters.app.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun Profile.launch(minecraftCredentials: MinecraftCredentials, xSTSCredentials: XBoxAuthResponse, accountInfo: MinecraftProfile) {
    val startArgs = buildList {
        add("java")
        addVanillaArguments(this@launch)
        addFabricArguments(this@launch)

        add("-Dminecraft.launcher.brand=headquarters")
        add("-Dminecraft.launcher.version=1.0.0")
    }

    withContext(Dispatchers.IO) {
        val process = ProcessBuilder().command(startArgs).start()
        appScope.launch {
            while (process.isAlive) {
                println(process.inputReader().readLines())
            }
        }
    }
}

private fun MutableList<String>.replaceVariables(credentials: MinecraftCredentials, xSTSCredentials: XBoxAuthResponse, accountInfo: MinecraftProfile) {
    val replacements = mapOf<String, String>(
        "\${auth_player_name}" to "acc.username",
        "" to ""
    )
    forEach {

    }
}