package dev.nyon.headquarters.app

import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.connector.fabric.FabricConnector
import dev.nyon.headquarters.connector.modrinth.ModrinthConnector
import dev.nyon.headquarters.connector.mojang.MojangConnector
import dev.nyon.headquarters.connector.mojang.models.`package`.Os
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

val appScope = CoroutineScope(Dispatchers.Default)
val runningDir = Path("${System.getProperty("user.home")}/headquarters/").createDirectories()
val librariesDir = runningDir.resolve("libraries/").createDirectories()
val assetsDir = runningDir.resolve("assets/").createDirectories().also {
    it.resolve("indexes/").createDirectories()
    it.resolve("log_configs/").createDirectories()
    it.resolve("objects/").createDirectories()
    it.resolve("skins/").createDirectories()
}
val javaVersionsDir = runningDir.resolve("java-versions/").createDirectories()
val os = Os.values().find { System.getProperty("os.name").lowercase().startsWith(it.name.lowercase()) } ?: error("cannot determine os!")
val arch = run {
    val arch = System.getProperty("sun.arch.data.model") ?: error("Could not find system property for core architecture 'os.arch'!")
    if (arch.toIntOrNull() == null) return@run arch
    return@run "x$arch"
}
const val version = "1.0.0"
private val ktorClientJson = Json {
    ignoreUnknownKeys = true
}
val ktorClient = HttpClient(CIO) {
    install (ContentNegotiation) {
        json(ktorClientJson)
    }
}
val modrinthConnector: ModrinthConnector = ModrinthConnector(ktorClient, ktorClientJson)
val fabricConnector: FabricConnector = FabricConnector(ktorClient, ktorClientJson)
val mojangConnector: MojangConnector = MojangConnector(ktorClient, ktorClientJson)

fun initApp() {
    realm
}