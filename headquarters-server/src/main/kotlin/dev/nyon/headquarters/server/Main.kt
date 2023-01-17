package dev.nyon.headquarters.server

import dev.nyon.headquarters.server.routings.configureProfileRoute
import dev.nyon.headquarters.server.routings.configureUserLoginRoot
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

val redirects = mutableMapOf<String, String>()
val httpClient = HttpClient()
val json = Json

fun main() {
    embeddedServer(CIO, port = System.getenv("HTTP_SERVER_PORT").toInt(), module = Application::myApplicationModule)
}

fun Application.myApplicationModule() {
    install(Authentication) {
        oauth("auth-oauth-github") {
            urlProvider = { "https://api.nyon.dev/headquarters/oauth" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "github",
                    authorizeUrl = "https://github.com/login/oauth/authorize",
                    accessTokenUrl = "https://github.com/login/oauth/access_token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GITHUB_CLIENT_ID"),
                    clientSecret = System.getenv("GITHUB_CLIENT_SECRET"),
                    onStateCreated = { call, state ->  
                        redirects[state] = call.request.queryParameters["redirectUrl"]!!
                    }
                )
            }
        }
    }

    routing {
        configureProfileRoute()
        configureUserLoginRoot()
    }
}