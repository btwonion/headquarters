package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.launcher.auth.MinecraftAccountInfo
import dev.nyon.headquarters.app.launcher.auth.MinecraftAuth
import dev.nyon.headquarters.app.profile.*
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

suspend fun Profile.launch(
    accountInfo: MinecraftAccountInfo
) {
    println("Running fileCheck")
    fileCheck()
    println("FileCheck complete")

    val startArgs = buildList {
        add(
            javaVersionsDir.resolve("java_${minecraftVersion.javaVersion.majorVersion}/").listDirectoryEntries().first()
                .resolve("bin/java").absolutePathString()
        )
        addVanillaArguments(this@launch) {
            if (loader == Loader.Fabric) addFabricArguments(this@launch)
            if (loader == Loader.Quilt) addQuiltArguments(this@launch)
        }

        replaceVariables(this@launch, accountInfo)
    }

    withContext(Dispatchers.IO) {
        ProcessBuilder().command(startArgs).start()
    }
}

suspend fun Profile.fileCheck() {
    assureJavaVersion()
    assureAssets()
    assureLauncherLibraries()
    assureMojangLibraries()
}

private fun MutableList<String>.replaceVariables(
    profile: Profile,
    accountInfo: MinecraftAccountInfo
) {
    val replacements = mapOf(
        "\${auth_player_name}" to accountInfo.username,
        "\${version_name}" to profile.loaderProfile.id,
        "\${game_directory}" to profile.profileDir.absolutePathString(),
        "\${assets_root}" to assetsDir.absolutePathString(),
        "\${assets_index_name}" to profile.minecraftVersion.assetIndex.id,
        "\${auth_uuid}" to accountInfo.uuid.toString(),
        "\${auth_access_token}" to accountInfo.accessToken,
        "\${clientid}" to MinecraftAuth.clientID,
        "\${auth_xuid}" to accountInfo.uhs,
        "\${user_type}" to "msa",
        "\${version_type}" to MinecraftVersionType::class.java.getDeclaredField(profile.minecraftVersion.type.name)
            .getAnnotation(SerialName::class.java).value,
        "\${natives_directory}" to librariesDir.absolutePathString(),
        "\${launcher_name}" to "headquarters",
        "\${launcher_version}" to version,
        "\${classpath}" to kotlin.run {
            buildList {
                profile.loaderProfile.libraries.forEach {
                    val split = it.name.split(":")
                    val fileName = "${split[1]}-${split[2]}.jar"
                    val artifactPath =
                        librariesDir.resolve("${split[0].replace(".", "/")}/${split[1]}/${split[2]}/$fileName")
                    add(artifactPath.absolutePathString())
                }

                profile.minecraftVersion.libraries.filter {
                    if (it.rules == null) return@filter true
                    if (it.rules!!.any { rule -> rule.os?.name == os }) return@filter true
                    false
                }.map { it.downloads.artifact }.forEach {
                    if (it.path != null) add(librariesDir.resolve(it.path!!).absolutePathString())
                }

                add(profile.profileDir.resolve("client.jar").absolutePathString())
            }.joinToString((":"))
        },
        "\${path}" to profile.profileDir.resolve(profile.minecraftVersion.logging.client!!.file.url.takeLastWhile { it != '/' })
            .absolutePathString()
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