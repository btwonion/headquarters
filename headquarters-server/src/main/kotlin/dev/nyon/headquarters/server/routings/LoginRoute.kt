package dev.nyon.headquarters.server.routings

import dev.nyon.headquarters.api.user.User
import dev.nyon.headquarters.server.database.users
import dev.nyon.headquarters.server.httpClient
import dev.nyon.headquarters.server.json
import dev.nyon.headquarters.server.redirects
import dev.nyon.headquarters.server.util.generateAndCheckID
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.litote.kmongo.eq

@Serializable
data class UserSession(val state: String, val token: String)

fun Routing.configureUserLoginRoot() {
    authenticate("auth-oauth-github") {
        get("/login") {}

        get("/oauth") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            call.sessions.set(UserSession(principal!!.state!!, principal.accessToken))
            val githubUser: JsonObject = json.encodeToJsonElement(httpClient.get {
                url(Url("https://api.github.com/user"))
                header("Accept", "application/vnd.github+json")
                header("Authorization", "Bearer ${principal.accessToken}")
                header("X-GitHub-Api-Version", "2022-11-28")
            }.bodyAsText()).jsonObject

            val id = githubUser["id"]!!.jsonPrimitive.content
            if (users.findOne(User::githubID eq id) != null) {
                val user = User(generateAndCheckID(8, users), id, listOf())
                users.insertOne(user)
            }

            val redirect = redirects[principal.state!!]
            call.respondRedirect(redirect!!)
        }
    }
}