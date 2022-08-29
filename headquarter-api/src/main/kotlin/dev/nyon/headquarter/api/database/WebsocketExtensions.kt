package dev.nyon.headquarter.api.database

import dev.nyon.headquarter.api.common.env
import dev.nyon.headquarter.api.distribution.Node
import dev.nyon.headquarter.api.networking.NetworkMessage
import dev.nyon.headquarter.api.networking.NodeRequest
import dev.nyon.headquarter.api.networking.NodeRequestAnswer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import java.util.*

val webClient = HttpClient(CIO) {
    install(WebSockets)
}

suspend inline fun listenChannel(channel: String, crossinline block: suspend DefaultClientWebSocketSession.() -> Unit) {
    webClient.webSocket(
        method = HttpMethod.Get,
        host = env("WEBSOCKET_CLIENT_ADDRESS"),
        port = env("WEBSOCKET_PORT").toInt(),
        path = "/$channel"
    ) {
        send(Frame.Text("jo"))
        block.invoke(this)
    }
}

suspend fun requestNode(uuid: UUID, block: Node.() -> Unit) {
    listenChannel("node") {
        sendSerialized(NodeRequest(uuid))
        for (frame in incoming) {
            if (receiveDeserialized<NetworkMessage>() !is NodeRequestAnswer) continue
            block.invoke(receiveDeserialized<NodeRequestAnswer>().node)
            return@listenChannel
        }
    }
}

suspend fun send(channel: String, frame: Frame) {
    webClient.webSocket(
        method = HttpMethod.Get,
        host = env("WEBSOCKET_CLIENT_ADDRESS"),
        port = env("WEBSOCKET_PORT").toInt(),
        path = "/$channel"
    ) {
        send(frame)
    }
}

suspend inline fun listen(crossinline session: suspend DefaultClientWebSocketSession.() -> Unit) =
    listenChannel("network", session)