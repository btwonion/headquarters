package dev.nyon.headquarters.app

import dev.nyon.headquarters.app.profile.local.realm
import dev.nyon.headquarters.connector.modrinth.ModrinthConnector
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Files.write
import java.nio.file.Path
import kotlin.io.path.*

lateinit var connector: ModrinthConnector
val runningDir = Path("${System.getProperty("user.home")}/headquarters/").createDirectories()
private val ktorClientJson = Json {
    ignoreUnknownKeys = true
}
val ktorClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(ktorClientJson)
    }
}

fun initApp() {
    realm

    connector = ModrinthConnector(ktorClient, ktorClientJson)
}

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

        val partPath = path.parent!!.resolve(path.name + ".part")

        try {
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_HTTP_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    write(partPath, packet.readBytes())
                }
            }
            partPath.moveTo(path, overwrite = true)

            downloadProgress?.invoke(1.0)
        } finally {
            partPath.deleteIfExists()
        }
    }
}