import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.URI
import kotlin.time.Duration.Companion.seconds

suspend fun main() {
    val client = HttpClient(CIO)

    val getRequest = client.get {
        url("http://0.0.0.0:8080/headquarters/login")
    }

    println(getRequest)

    val response = client.post {
        url(getRequest.request.url)

        accept(ContentType.Application.Json)

        parameter("client_id", System.getenv("CLIENT_ID"))
    }

    println(response.bodyAsText())

    val codeRequestResponse = Json.decodeFromString<CodeRequestResponse>(response.bodyAsText())

    withContext(Dispatchers.IO) {
        Desktop.getDesktop().browse(URI(codeRequestResponse.verification_uri))
    }

    println(codeRequestResponse.user_code)

    delay(15.seconds)

    val accessTokenResponse = client.post {
        url("https://github.com/login/oauth/access_token")

        parameter("client_id", System.getenv("CLIENT_ID"))
        parameter("device_code", codeRequestResponse.device_code)
        parameter("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
    }

    println(accessTokenResponse.bodyAsText())
}

@Serializable
data class CodeRequestResponse(
    val device_code: String,
    val user_code: String,
    val verification_uri: String,
    val expires_in: Int,
    val interval: Int
)