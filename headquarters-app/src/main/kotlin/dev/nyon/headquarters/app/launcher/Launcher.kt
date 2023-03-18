package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.assetsDir
import dev.nyon.headquarters.app.javaVersionsDir
import dev.nyon.headquarters.app.launcher.auth.MinecraftAuth
import dev.nyon.headquarters.app.launcher.auth.MinecraftCredentials
import dev.nyon.headquarters.app.launcher.auth.MinecraftProfile
import dev.nyon.headquarters.app.launcher.auth.XBoxAuthResponse
import dev.nyon.headquarters.app.librariesDir
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.version
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries

suspend fun Profile.launch(
    minecraftCredentials: MinecraftCredentials,
    xSTSCredentials: XBoxAuthResponse,
    mcProfile: MinecraftProfile
) {
    val startArgs = buildList {
        add(
            javaVersionsDir.resolve("java_${minecraftVersion.javaVersion.majorVersion}/").listDirectoryEntries().first()
                .resolve("bin/java").absolutePathString()
        )
        addVanillaArguments(this@launch) {
            if (loader == Loader.Fabric) addFabricArguments(this@launch)
        }

        replaceVariables(this@launch, minecraftCredentials, xSTSCredentials, mcProfile, minecraftVersion)
    }

    println(startArgs)

    withContext(Dispatchers.IO) {
        val process = ProcessBuilder().command(startArgs)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT).start()
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
        "\${assets_root}" to assetsDir.absolutePathString(),
        "\${assets_index_name}" to minecraftVersionPackage.assetIndex.id,
        "\${auth_uuid}" to mcProfile.id.toString(),
        "\${auth_access_token}" to credentials.accessToken,
        "\${clientid}" to MinecraftAuth.clientID,
        "\${auth_xuid}" to xSTSCredentials.displayClaims.xui.first().uhs,
        "\${user_type}" to "msa",
        "\${version_type}" to MinecraftVersionType::class.java.getDeclaredField(minecraftVersionPackage.type.name)
            .getAnnotation(SerialName::class.java).value,
        "\${natives_directory}" to librariesDir.absolutePathString(),
        "\${launcher_name}" to "headquarters",
        "\${launcher_version}" to version,
        "\${classpath}" to kotlin.run {
            val entries = mutableListOf<Path>()
            val filesToLook = librariesDir.listDirectoryEntries().toMutableList()

            do {
                listOf(filesToLook).flatten().forEach {
                    filesToLook.remove(it)
                    if (it.isRegularFile()) entries.add(it)
                    else filesToLook.addAll(it.listDirectoryEntries())
                }
            } while (filesToLook.isNotEmpty())

            entries.add(profile.profileDir.resolve("client.jar"))

            entries.joinToString(":") {
                it.absolutePathString()
            }
        }
    )

    forEachIndexed { index, original ->
        for ((key, value) in replacements) {
            if (original.contains(key)) {
                this@replaceVariables[index] = original.replace(key, value)
                break
            }
        }
    }
}