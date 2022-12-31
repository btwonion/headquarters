package dev.nyon.headquarters.server

import dev.nyon.headquarters.server.routings.configureProfileRoute
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = System.getenv("HTTP_SERVER_PORT").toInt(), module = Application::myApplicationModule)
}

fun Application.myApplicationModule() {
    routing {
        configureProfileRoute()
    }
}