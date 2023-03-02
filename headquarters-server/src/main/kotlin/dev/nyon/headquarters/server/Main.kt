package dev.nyon.headquarters.server

import dev.nyon.headquarters.api.user.User
import dev.nyon.headquarters.server.routings.configureProfileRoute
import dev.nyon.headquarters.server.routings.configureUserLoginRoot
import dev.nyon.headquarters.server.session.UserSession
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import kotlin.time.Duration.Companion.days

val httpClient = HttpClient(CIO) {
    this.install(ContentNegotiation) {
        json()
    }
}
val json = Json

fun main() {
    embeddedServer(
        Netty, port = System.getenv("HTTP_SERVER_PORT").toInt(), module = Application::myApplicationModule
    ).start(wait = true)
}

fun Application.myApplicationModule() {
    install(Authentication) {
        oauth("auth-oauth-github") {
            client = httpClient
            urlProvider = { "http://127.0.0.1:8080/headquarters/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "github",
                    authorizeUrl = "https://github.com/login/device/code",
                    accessTokenUrl = "https://github.com/login/oauth/access_token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GITHUB_CLIENT_ID"),
                    clientSecret = System.getenv("GITHUB_CLIENT_SECRET")
                )
            }
        }
    }

    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/headquarters/"
            cookie.maxAge = 30.days
        }
        cookie<User>("user") {
            cookie.path = "/headquarters/"
            cookie.maxAge = 30.days
        }
    }

    install(CallLogging) {
        level = Level.DEBUG
        //filter { call -> call.request.path().startsWith("/headquarters/") }
    }

    routing {
        route("/headquarters") {
            configureProfileRoute()
            configureUserLoginRoot()
        }
    }
}