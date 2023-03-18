package dev.nyon.headquarters.app.util

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.*

suspend inline fun HttpClient.downloadFile(
    url: Url,
    path: Path,
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
) = prepareRequest(url) {
    builder(this)
}.execute { response ->
    withContext(Dispatchers.IO) {
        val channel = response.bodyAsChannel()

        val partPath = path.parent!!.resolve(path.name + ".part")
        if (partPath.notExists()) partPath.createFile()

        try {
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(200000000.toLong())
                while (!packet.isEmpty) {
                    partPath.writeBytes(packet.readBytes())
                }
            }
            partPath.moveTo(path, overwrite = true)
        } finally {
            partPath.deleteIfExists()
        }
    }
}