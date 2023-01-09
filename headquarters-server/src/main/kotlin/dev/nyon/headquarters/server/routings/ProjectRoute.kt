package dev.nyon.headquarters.server.routings

import dev.nyon.headquarters.api.Profile
import dev.nyon.headquarters.api.Visibility
import dev.nyon.headquarters.server.database.profiles
import dev.nyon.headquarters.server.util.generateProfileID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import org.litote.kmongo.eq

fun Routing.configureProfileRoute() {
    route("/profile") {
        get("{id}") {
            val id = call.parameters["id"]!!
            val profile = profiles.findOne(Profile::id eq id)

            if (profile == null) call.respond(HttpStatusCode.NotFound)
            else if (profile.visibility == Visibility.Private) call.respond(HttpStatusCode.Unauthorized)
            else call.respond(profile)
        }

        delete("{id}") {
            val id = call.parameters["id"]!!

            profiles.deleteOne(Profile::id eq id)
            call.respondText("Project removed successfully")
        }

        post {
            val profile = call.receive<Profile>()

            if (profiles.findOne(Profile::id eq profile.id) != null) call.respond(HttpStatusCode.Conflict)
            else {
                launch {
                    profile.id = generateProfileID(8)
                    profiles.insertOne(profile)
                    call.respond(profile)
                }
            }
        }
    }
}