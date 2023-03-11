package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.launcher.auth.MicrosoftAuth
import dev.nyon.headquarters.app.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


suspend fun Profile.launch(credentials: MicrosoftAuth.MicrosoftAccountInfo) {
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

private fun MutableList<String>.replaceVariables() {
    val replacements = mapOf<String, String>(
        "\${auth_player_name}" to ""
    )
    forEach {

    }
}