package dev.nyon.headquarters.server.routings

import dev.nyon.headquarters.api.Profile
import dev.nyon.headquarters.api.Visibility
import dev.nyon.headquarters.api.user.User
import dev.nyon.headquarters.server.database.profiles
import dev.nyon.headquarters.server.util.generateAndCheckID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.launch
import org.litote.kmongo.eq

fun Route.configureProfileRoute() {
    authenticate("auth-oauth-github") {
        get("/profile/{id}") {
            println("dawdadw")
            val id = call.parameters["id"]!!
            val profile = profiles.findOne(Profile::id eq id)

            if (profile == null) call.respond(HttpStatusCode.NotFound)
            else if (profile.visibility == Visibility.Private) call.respond(HttpStatusCode.Unauthorized)
            else call.respond(profile)
        }

        delete("/profile/{id}") {
            val id = call.parameters["id"]!!

            profiles.deleteOne(Profile::id eq id)
            call.respondText("Project removed successfully")
        }

        post("/profile") {
            println("sadawd")
            val user = call.sessions.get<User>()
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            println(user.id)
            val profile = call.receive<Profile>()

            if (profiles.findOne(Profile::id eq profile.id) != null) call.respond(HttpStatusCode.Conflict)
            else {
                launch {
                    profile.id = generateAndCheckID(8, profiles)
                    profiles.insertOne(profile)
                    call.respond(profile)
                }
            }
        }
    }
}