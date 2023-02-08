import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun main() {
    val client = HttpClient(CIO)

    val getRequest = client.get {
        url(Url("https://api.nyon.dev/headquarters/login"))
    }

    val response = client.post {
        url(getRequest.request.url)

        accept(ContentType.Application.Json)

        parameter("client_id", System.getenv("CLIENT_ID"))
    }

    println(response.bodyAsText())
}