package dev.nyon.headquarters.app

import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.connector.fabric.FabricConnector
import dev.nyon.headquarters.connector.modrinth.ModrinthConnector
import dev.nyon.headquarters.connector.mojang.MojangConnector
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

val runningDir = Path("${System.getProperty("user.home")}/headquarters/").createDirectories()
private val ktorClientJson = Json {
    ignoreUnknownKeys = true
}
val ktorClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(ktorClientJson)
    }
}
val modrinthConnector: ModrinthConnector = ModrinthConnector(ktorClient, ktorClientJson)
val fabricConnector: FabricConnector = FabricConnector(ktorClient, ktorClientJson)
val mojangConnector: MojangConnector = MojangConnector(ktorClient, ktorClientJson)

fun initApp() {
    realm
}