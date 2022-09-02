package dev.nyon.headquarter.server

import dev.nyon.headquarter.api.common.env
import dev.nyon.headquarter.api.distribution.Client
import dev.nyon.headquarter.api.networking.NetworkMessage
import dev.nyon.headquarter.api.networking.ServiceModify
import dev.nyon.headquarter.api.networking.ServiceRequest
import dev.nyon.headquarter.api.networking.ServiceRequestAnswer
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
import kotlinx.coroutines.flow.toList
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

                webSocket("/db") {
                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        when (val message = receiveDeserialized<NetworkMessage>()) {
                            is ServiceRequest -> {
                                newScope {
                                    clientRealm.query<Client>("uuid = '${message.uuid}'").asFlow().collect {
                                        sendSerialized(ServiceRequestAnswer(it.list.getOrNull(0)))
                                    }
                                }
                            }

                            is ServiceModify -> {
                                newScope {
                                    if (message.service == null && message.uuid != null) clientRealm.write {
                                        val query = query<Client>("uuid = '${message.uuid}")
                                        delete(query)
                                    }
                                    if (message.service != null) {
                                        clientRealm.write {
                                            launch {
                                                val query = query<Client>("uuid = '${message.service!!.uuid}'")
                                                if (query.asFlow().toList().isNotEmpty()) delete(query)
                                                copyToRealm(message.service!!)
                                            }
                                        }
                                    }

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