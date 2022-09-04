package dev.nyon.headquarter.api.networking

import dev.nyon.headquarter.api.common.env
import dev.nyon.headquarter.api.distribution.Client
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import java.util.*

val webClient = HttpClient(CIO) {
    install(WebSockets)
}

suspend fun requestService(uuid: UUID, block: Client?.() -> Unit) {
    webSocketClient("db") {
        sendSerialized(ServiceRequest(uuid))
        for (frame in incoming) {
            if (receiveDeserialized<NetworkMessage>() !is ServiceRequestAnswer) continue
            block.invoke(receiveDeserialized<ServiceRequestAnswer>().service)
            return@webSocketClient
        }
    }
}

suspend fun send(channel: String, frame: Frame) = webSocketClient(channel) {
    send(frame)
}

suspend fun sendSerialized(channel: String, text: @Serializable NetworkMessage) =
    webSocketClient(channel) { sendSerialized(text) }

suspend inline fun webSocketClient(
    channel: String, crossinline block: suspend DefaultClientWebSocketSession.() -> Unit
) {
    webClient.webSocket(
        method = HttpMethod.Get,
        host = env("INTERNAL_SERVER_HOST"),
        port = env("INTERNAL_SERVER_PORT").toInt(),
        path = "/$channel"
    ) { block.invoke(this) }
}