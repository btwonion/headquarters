import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun main() {
    val client = HttpClient(CIO)

    val response = client.post {
        url(Url("https://github.com/login/device/code"))
        header("Accept", "application/json")

        parameter("client_id", System.getenv("CLIENT_ID"))
    }

    println(response.bodyAsText())
}