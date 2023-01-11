import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

suspend fun main() {
    val client = HttpClient()

    val response = client.post {
        url(Url("https://github.com/login/oauth/access_token"))
        header("Accept", "application/json")

        parameter("client_id", System.getenv("CLIENT_ID"))
        parameter("device_code", System.getenv("DEVICE_CODE"))
        parameter("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
    }

    try {
        Json { ignoreUnknownKeys = true }.decodeFromString<GithubAccess>(response.bodyAsText())
    } catch (e: Exception) {
        println(response.bodyAsText())
        exitProcess(0)
    }

    val access = Json { ignoreUnknownKeys = true }.decodeFromString<GithubAccess>(response.bodyAsText())

    println(client.get {
        url(Url("https://api.github.com/user"))
        header("Accept", "application/vnd.github+json")
        header("Authorization", "Bearer ${access.accessToken}")
        header("X-GitHub-Api-Version", "2022-11-28")
    }.bodyAsText())
}

@Serializable
data class GithubAccess(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    val scope: String
)