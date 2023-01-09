import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun main() {
    val client = HttpClient()

    val response = client.post {
        url(Url("https://github.com/login/device/code"))
        header("Accept", "application/json")

        parameter("client_id", "64d230b0fb9e74760b5f")
    }

    println(response.bodyAsText())
}