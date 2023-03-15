package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.launcher.auth.MinecraftAuth
import dev.nyon.headquarters.app.launcher.auth.MinecraftCredentials
import dev.nyon.headquarters.app.launcher.auth.MinecraftProfile
import dev.nyon.headquarters.app.launcher.auth.XBoxAuthResponse
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.version
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

suspend fun Profile.launch(
    minecraftCredentials: MinecraftCredentials,
    xSTSCredentials: XBoxAuthResponse,
    mcProfile: MinecraftProfile
) {
    val startArgs = buildList {
        add("java")
        addVanillaArguments(this@launch)
        addFabricArguments(this@launch)

        replaceVariables(this@launch, minecraftCredentials, xSTSCredentials, mcProfile, minecraftVersion)
    }

    println(startArgs)

    withContext(Dispatchers.IO) {
        val process = ProcessBuilder().command(startArgs).start()
        appScope.launch(context = Dispatchers.IO) {
            while (process.isAlive) {
                println(process.inputReader().readLines())
            }
        }
    }
}

private fun MutableList<String>.replaceVariables(
    profile: Profile,
    credentials: MinecraftCredentials,
    xSTSCredentials: XBoxAuthResponse,
    mcProfile: MinecraftProfile,
    minecraftVersionPackage: VersionPackage
) {
    val replacements = mapOf(
        "\${auth_player_name}" to mcProfile.name,
        "\${version_name}" to profile.loaderProfile.id,
        "\${game_directory}" to profile.profileDir.absolutePathString(),
        "\${assets_root}" to profile.profileDir.resolve("assets").absolutePathString(),
        "\${assets_index_name}" to minecraftVersionPackage.assetIndex.id,
        "\${auth_uuid}" to mcProfile.id.toString(),
        "\${auth_access_token}" to credentials.accessToken,
        "\${clientid}" to MinecraftAuth.clientID,
        "\${auth_xuid}" to xSTSCredentials.displayClaims.xui.first().uhs,
        "\${user_type}" to "msa",
        "\${version_type}" to MinecraftVersionType::class.java.getDeclaredField(minecraftVersionPackage.type.name)
            .getAnnotation(SerialName::class.java).value,
        "\${natives_directory}" to System.getProperty("java.home"),
        "\${launcher_name}" to "headquarters",
        "\${launcher_version}" to version,
        "\${classpath}" to profile.profileDir.resolve("libraries/").listDirectoryEntries().joinToString(":") {
            it.absolutePathString()
        }
    )

    forEachIndexed { index, original ->
        if (replacements.contains(original)) {
            this.removeAt(index)
            this.add(index, replacements[original]!!)
        }
    }
}