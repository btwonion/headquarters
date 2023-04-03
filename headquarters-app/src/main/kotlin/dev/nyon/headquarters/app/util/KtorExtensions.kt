package dev.nyon.headquarters.app.util

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.writeBytes

suspend inline fun HttpClient.downloadFile(
    url: Url,
    path: Path,
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
) = prepareRequest(url) {
    builder(this)
}.execute { response ->
    withContext(Dispatchers.IO) {
        val channel = response.bodyAsChannel()
        path.createFile().writeBytes(channel.toByteArray())
    }
}