package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.mojangConnector
import dev.nyon.headquarters.app.profile.LocalProfile
import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.app.profile.testModProfile
import dev.nyon.headquarters.app.runningDir
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.absolutePathString
import kotlin.io.path.listDirectoryEntries

suspend fun main() {
    realm.query<LocalProfile>().find()[0].launch()
}

suspend fun LocalProfile.launch() {
    val profileDir = runningDir.resolve("profiles/${this.name}/")
    val pkg = mojangConnector.getVersionPackage(testModProfile.minecraftVersion.id)
        ?: error("cannot find needed version package")
    val startArgs = buildList<String> {
        add("java")
        add("-Xss1M")
        add("-Djava.library.path=.")
        add("-Dminecraft.launcher.brand=headquarters")
        add("-Dminecraft.launcher.version=1.0.0")
        add("-cp")
        add(profileDir.resolve("libraries/").listDirectoryEntries().joinToString(":") {
            it.absolutePathString()
        })
        add("-DFabricMcEmu=net.minecraft.client.main.Main")
        add("-Xmx4G")
        add("-XX:+UnlockExperimentalVMOptions")
        add("-XX:+UseG1GC")
        add("-Dlog4j.configurationFile=net.fabricmc.loader.impl.launch.knot.KnotClient")
        add("--gameDir")
        add(profileDir.absolutePathString())
        add("--assetsDir")
        add(profileDir.resolve("assets/").absolutePathString())
        add("--assetIndex")
        add(pkg.assetIndex.id)
        add("--userType")
        add("msa")
        add("--versionType")
    }

    withContext(Dispatchers.IO) {
        ProcessBuilder().command(startArgs).start()
    }
}