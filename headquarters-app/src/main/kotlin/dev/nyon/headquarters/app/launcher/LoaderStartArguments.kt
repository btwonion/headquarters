package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.mojangConnector
import dev.nyon.headquarters.app.profile.Profile
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

fun MutableList<String>.addFabricArguments(profile: Profile) {
    add("-DFabricMcEmu=net.minecraft.client.main.Main")
    add("-Dlog4j.configurationFile=net.fabricmc.loader.impl.launch.knot.KnotClient") // must be changed for vanilla
}

suspend fun MutableList<String>.addVanillaArguments(profile: Profile) {
    val pkg = mojangConnector.getVersionPackage(profile.minecraftVersion.id)
        ?: error("Cannot find VersionPackage for minecraft version '${profile.minecraftVersion.id}'")

    add("-Xss1M")
    add("-Djava.library.path=.")
    add("-cp")
    add(profile.profileDir.resolve("libraries/").listDirectoryEntries().joinToString(":") {
        it.absolutePathString()
    })
    add("-Xmx${profile.memory}G")
    add("-XX:+UnlockExperimentalVMOptions")
    add("-XX:+UseG1GC")
    add("--gameDir")
    add(profile.profileDir.absolutePathString())
    add("--assetsDir")
    add(profile.profileDir.resolve("assets/").absolutePathString())
    add("--assetIndex")
    add(pkg.assetIndex.id)
    add("--userType")
    add("msa")
    add("--versionType")
}