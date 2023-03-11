package dev.nyon.headquarters.app.util

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

suspend inline fun HttpClient.downloadFile(
    url: Url,
    path: Path,
    noinline downloadProgress: (suspend (Double) -> Unit)? = null,
    crossinline builder: HttpRequestBuilder.() -> Unit = {},
) = prepareRequest(url) {
    builder(this)

    if (downloadProgress != null) {
        onDownload { bytesSentTotal, contentLength ->
            val progress = bytesSentTotal.toDouble() / contentLength.toDouble()
            if (!progress.isNaN()) {
                downloadProgress(progress)
            }
        }
    }
}.execute { response ->
    withContext(Dispatchers.IO) {
        val channel = response.bodyAsChannel()

        val partPath = path.parent!!.resolve(path.name + ".part").createFile()

        try {
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_HTTP_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    partPath.writeBytes(packet.readBytes())
                }
            }
            partPath.moveTo(path, overwrite = true)

            downloadProgress?.invoke(1.0)
        } finally {
            partPath.deleteIfExists()
        }
    }
}