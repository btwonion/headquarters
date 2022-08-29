package dev.nyon.headquarter.server

import dev.nyon.headquarter.api.common.env
import dev.nyon.headquarter.api.distribution.Node
import dev.nyon.headquarter.api.networking.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.time.Duration

fun CoroutineScope.configureWebsockets() {
    launch {
        embeddedServer(
            Netty,
            port = env("WEBSOCKET_PORT").toInt(),
            host = env("WEBSOCKET_HOST"),
            parentCoroutineContext = Dispatchers.Default + SupervisorJob()
        ) {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                val networkFlow = MutableSharedFlow<Frame>()

                webSocket("/network") {
                    launch {
                        networkFlow.collect(::send)
                    }
                    for (frame in incoming) {
                        CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
                            networkFlow.emit(frame)
                        }
                    }
                }

                webSocket("/nodedb") {
                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        if (frame.readText() == "jo")continue
                        when (val message = receiveDeserialized<NetworkMessage>()) {
                            is NodeRequest -> {
                                newScope {
                                    nodeRealm.query<Node>("uuid = '${message.uuid}'").asFlow().collect {
                                        sendSerialized(NodeRequestAnswer(it.list[0]))
                                    }
                                }
                            }

                            is NodeDelete -> {
                                newScope {
                                    nodeRealm.write {
                                        val query = query<Node>("uuid = '${message.uuid}'")
                                        delete(query)
                                    }
                                }
                            }

                            is NodeUpdate -> {
                                newScope {
                                    nodeRealm.write {
                                        val query = query<Node>("uuid = '${message.node.uuid}'")
                                        delete(query)
                                        copyToRealm(message.node)
                                    }
                                }
                            }

                            is NodeCreate -> {
                                newScope {
                                    nodeRealm.write { copyToRealm(message.node) }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }.start(true)
    }
}

private fun newScope(block: suspend CoroutineScope.() -> Unit) {
    val context = Dispatchers.Default + SupervisorJob()
    CoroutineScope(context).launch(block = block)
}