package dev.nyon.headquarters.app

import dev.nyon.headquarters.app.launcher.auth.encryptionKey
import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.app.util.encryptBlowfish
import dev.nyon.headquarters.connector.fabric.FabricConnector
import dev.nyon.headquarters.connector.modrinth.ModrinthConnector
import dev.nyon.headquarters.connector.mojang.MojangConnector
import dev.nyon.headquarters.connector.mojang.models.`package`.Os
import dev.nyon.headquarters.connector.quilt.QuiltConnector
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.*

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
val accountsFile: Path = runningDir.resolve("minecraft-accounts").also {
    if (it.notExists()) {
        it.createFile()
        it.writeText(encryptBlowfish(encryptionKey, "[]"))
    }
}
val os = Os.values().find { System.getProperty("os.name").lowercase().startsWith(it.name.lowercase()) }
    ?: error("Cannot determine os!")
val arch = run {
    val arch = System.getProperty("sun.arch.data.model")
        ?: error("Could not find system property for core architecture 'os.arch'!")
    if (arch.toIntOrNull() == null) return@run arch
    return@run "x$arch"
}
const val version = "1.0.0"
val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
val ktorClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(json)
    }
}
val modrinthConnector: ModrinthConnector = ModrinthConnector(ktorClient, json)
val fabricConnector: FabricConnector = FabricConnector(ktorClient, json)
val mojangConnector: MojangConnector = MojangConnector(ktorClient, json)
val quiltConnector: QuiltConnector = QuiltConnector(ktorClient, json)

fun initApp() {
    realm
}