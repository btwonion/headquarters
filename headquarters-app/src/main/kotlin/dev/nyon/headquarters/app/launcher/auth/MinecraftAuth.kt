@file:Suppress("SpellCheckingInspection")

package dev.nyon.headquarters.app.launcher.auth

import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.util.decryptBlowfish
import dev.nyon.headquarters.app.util.encryptBlowfish
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.awt.Desktop
import java.util.*
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.minutes


class MinecraftAuth(private val callback: suspend MinecraftAccountInfo.() -> Unit) {

    companion object {
        const val clientID = "e16699bb-2aa8-46da-b5e3-45cbcce29091"
        private const val authorizeUrl = "https://login.live.com/oauth20_authorize.srf"
        private const val tokenUrl = "https://login.live.com/oauth20_token.srf"
        private const val xBoxAuthUrl = "https://user.auth.xboxlive.com/user/authenticate"
        private const val xSTSTokenUrl = "https://xsts.auth.xboxlive.com/xsts/authorize"
        private const val minecraftAccountRequestUrl =
            "https://api.minecraftservices.com/authentication/login_with_xbox"
        const val minecraftProfileRequestUrl = "https://api.minecraftservices.com/minecraft/profile"
        const val xSTSRelyingPartyUrl = "rp://api.minecraftservices.com/"
        const val relyingPartyUrl = "http://auth.xboxlive.com"
    }

    private val port = 25585
    private val state = UUID.randomUUID().toString()
    private val redirectUri = "http://localhost:$port/callback"
    private val server = embeddedServer(CIO, port = port) { configure() }

    suspend fun prepareLogIn() {
        val uri = URLBuilder(authorizeUrl).apply {
            parameters.append("client_id", clientID)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("scope", "XboxLive.signin offline_access")
            parameters.append("response_type", "code")
            parameters.append("state", state)
        }.build()

        withContext(Dispatchers.IO) {
            Desktop.getDesktop().browse(uri.toURI())
        }

        server.start(true)
    }

    private fun Application.configure() {
        routing {
            get("/callback") {
                appScope.launch {
                    authenticate(call.parameters["code"]!!)
                }

                call.respondText {
                    "You can close this window now!"
                }

                server.stop()
            }
        }
    }

    private suspend fun authenticate(code: String) {
        val msAccessTokenResponse = ktorClient.post(Url(tokenUrl)) {
            contentType(ContentType.Application.FormUrlEncoded)

            setBody(
                FormDataContent(
                    parametersOf(
                        "client_id" to listOf(clientID),
                        "grant_type" to listOf("authorization_code"),
                        "code" to listOf(code),
                        "redirect_uri" to listOf(redirectUri)
                    )
                )
            )
        }.body<MicrosoftAccessTokenResponse>()

        val xBoxAuthResponse = ktorClient.post(Url(xBoxAuthUrl)) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            setBody(json.encodeToString(XBoxAuthRequest(XBoxProperties(rpsTicket = "d=${msAccessTokenResponse.accessToken}"))))
        }.body<XBoxAuthResponse>()

        val xSTSTokenResponse = ktorClient.post(Url(xSTSTokenUrl)) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            setBody(
                json.encodeToString(
                    XSTSAuthRequest(
                        XSTSProperties(userTokens = listOf(xBoxAuthResponse.token))
                    )
                )
            )
        }.body<XBoxAuthResponse>()

        val minecraftAccountResponse = ktorClient.post(Url(minecraftAccountRequestUrl)) {
            contentType(ContentType.Application.Json)

            setBody(MinecraftAccountCredentialsRequest("XBL3.0 x=${xSTSTokenResponse.displayClaims.xui.first().uhs};${xSTSTokenResponse.token}"))
        }.body<MinecraftCredentials>()
        val expireDate = Clock.System.now() + minecraftAccountResponse.expiresIn.minutes

        val minecraftProfileResponse = ktorClient.get(Url(minecraftProfileRequestUrl)) {
            header("Authorization", "Bearer ${minecraftAccountResponse.accessToken}")
        }.body<MinecraftProfile>()

        callback(
            MinecraftAccountInfo(
                minecraftProfileResponse.id,
                minecraftAccountResponse.accessToken,
                expireDate,
                minecraftProfileResponse.name,
                xSTSTokenResponse.displayClaims.xui.first().uhs
            )
        )
    }
}


const val encryptionKey = "0123456789AB"
fun saveAccountsFile(accounts: List<MinecraftAccountInfo>) =
    accountsFile.writeText(encryptBlowfish(encryptionKey, json.encodeToString(accounts)))

fun readAccountsFile(): List<MinecraftAccountInfo> {
    val raw = accountsFile.readText()
    val decrypted = decryptBlowfish(encryptionKey, raw)
    val accounts: MutableList<MinecraftAccountInfo> = json.decodeFromString(decrypted)
    accounts.removeIf { it.expireDate < Clock.System.now() }
    return accounts
}