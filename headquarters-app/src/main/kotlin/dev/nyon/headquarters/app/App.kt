package dev.nyon.headquarters.app

import dev.nyon.headquarters.connector.modrinth.ModrinthConnector
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

lateinit var connector: ModrinthConnector

suspend fun initApp() {
    val ktorClientJson = Json {
        ignoreUnknownKeys = true
    }

    val ktorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(ktorClientJson)
        }
    }
    connector = ModrinthConnector(ktorClient, ktorClientJson)
}