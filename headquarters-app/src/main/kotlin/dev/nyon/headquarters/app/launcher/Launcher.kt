package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.profile.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.obsilabor.pistonmetakt.MicrosoftAuth


suspend fun Profile.launch(credentials: MicrosoftAuth.MicrosoftAccountInfo) {
    val startArgs = buildList {
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

private fun MutableList<String>.replaceVariables() {
    val replacements = mapOf<String, String>(
        "\${auth_player_name}" to ""
    )
    forEach {

    }
}