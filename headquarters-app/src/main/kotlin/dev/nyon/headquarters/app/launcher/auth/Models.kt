@file:Suppress("SpellCheckingInspection")
package dev.nyon.headquarters.app.launcher.auth

import dev.nyon.headquarters.app.util.OldUUIDSerializer
import dev.nyon.headquarters.app.util.UUIDSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.util.UUID

@Serializable
data class MicrosoftAccessTokenResponse(
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val scope: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("user_id")
    val userID: String
)

@Serializable
data class XBoxProperties(
    @SerialName("AuthMethod") val authMethod: String = "RPS",
    @SerialName("SiteName") val siteName: String = "user.auth.xboxlive.com",
    @SerialName("RpsTicket") val rpsTicket: String
)

@Serializable
data class XSTSProperties(
    @SerialName("SandboxId") val sandBoxID: String = "RETAIL",
    @SerialName("UserTokens") val userTokens: List<String>
)

@Serializable
data class XBoxAuthRequest(
    @SerialName("Properties") val properties: XBoxProperties,
    @SerialName("RelyingParty") val relyingParty: String = MinecraftAuth.relyingPartyUrl,
    @SerialName("TokenType") val tokenType: String = "JWT"
)

@Serializable
data class XSTSAuthRequest(
    @SerialName("Properties") val properties: XSTSProperties,
    @SerialName("RelyingParty") val relyingParty: String = MinecraftAuth.xSTSRelyingPartyUrl,
    @SerialName("TokenType") val tokenType: String = "JWT"
)

@Serializable
data class XBoxAuthResponse(
    @SerialName("IssueInstant") val issueInstant: Instant,
    @SerialName("NotAfter") val notAfter: Instant,
    @SerialName("Token") val token: String,
    @SerialName("DisplayClaims") val displayClaims: DisplayClaims
) {
    @Serializable
    data class DisplayClaims(val xui: List<UHS>)

    @Serializable
    data class UHS(val uhs: String)
}

@Serializable
data class MinecraftAccountCredentialsRequest(
    val identityToken: String,
    val ensureLegacyEnabled: Boolean = true
)

@Serializable
data class MinecraftCredentials(
    val username: @Serializable(with = UUIDSerializer::class) UUID,
    val roles: List<JsonObject>,
    val metadata: JsonObject,
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("token_type") val tokenType: String
)

@Serializable
data class MinecraftProfile(
    val id: @Serializable(with = OldUUIDSerializer::class) UUID,
    val name: String,
    val skins: List<Skin>,
    val capes: List<Cape>,
    val profileActions: JsonObject
) {
    @Serializable
    data class Skin(val id: @Serializable(with = UUIDSerializer::class) UUID, val state: String, val url: String, val variant: String)

    @Serializable
    data class Cape(val id: @Serializable(with = UUIDSerializer::class) UUID, val state: String, val url: String, val alias: String)
}