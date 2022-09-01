package dev.nyon.headquarter.server

import dev.nyon.headquarter.api.common.env
import dev.nyon.headquarter.api.distribution.Client
import dev.nyon.headquarter.api.networking.*
import dev.nyon.headquarter.api.player.NetworkPlayer
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

                webSocket("/playerDb") {
                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        when (val message = receiveDeserialized<NetworkMessage>()) {
                            is PlayerRequest -> {
                                newScope {
                                    playerRealm.query<NetworkPlayer>("uuid = '${message.uuid}'").asFlow().collect {
                                        sendSerialized(PlayerRequestAnswer(it.list[0]))
                                    }
                                }
                            }

                            is PlayerDelete -> {
                                newScope {
                                    playerRealm.write {
                                        val query = query<NetworkPlayer>("uuid = '${message.uuid}'")
                                        delete(query)
                                    }
                                }
                            }

                            is PlayerUpdate -> {
                                newScope {
                                    playerRealm.write {
                                        val query = query<NetworkPlayer>("uuid = '${message.player.uuid}'")
                                        delete(query)
                                        copyToRealm(message.player)
                                    }
                                }
                            }

                            is PlayerCreate -> {
                                newScope {
                                    playerRealm.write { copyToRealm(message.player) }
                                }
                            }

                            else -> {}
                        }
                    }
                }
                webSocket("/clientDb") {
                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        when (val message = receiveDeserialized<NetworkMessage>()) {
                            is ServiceRequest -> {
                                newScope {
                                    clientRealm.query<Client>("uuid = '${message.uuid}'").asFlow().collect {
                                            sendSerialized(ServiceRequestAnswer(it.list[0]))
                                        }
                                }
                            }

                            is ServiceDelete -> {
                                newScope {
                                    clientRealm.write {
                                        val query = query<Client>("uuid = '${message.uuid}'")
                                        delete(query)
                                    }
                                }
                            }

                            is ServiceUpdate -> {
                                newScope {
                                    clientRealm.write {
                                        val query = query<Client>("uuid = '${message.service.uuid}'")
                                        delete(query)
                                        copyToRealm(message.service)
                                    }
                                }
                            }

                            is ServiceCreate -> {
                                newScope {
                                    clientRealm.write { copyToRealm(message.service) }
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