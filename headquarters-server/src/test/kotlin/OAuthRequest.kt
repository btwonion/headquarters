import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun main() {
    val client = HttpClient {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    val response = client.post {
        url(Url("https://github.com/login/oauth/authorize"))
        header("Accept", "application/json")

        parameter("client_id", System.getenv("CLIENT_ID"))
    }

    println(response.bodyAsText())
}