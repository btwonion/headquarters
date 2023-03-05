import dev.nyon.headquarters.api.Profile
import dev.nyon.headquarters.api.Visibility
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Instant

suspend fun main() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    val createResponse = client.post("http://0.0.0.0:8080/headquarters/profile") {
        contentType(ContentType.Application.Json)

        setBody(Profile.ModProfile(
            "sadawa",
            "btwonion",
            MinecraftVersion(
                "1.19.3",
                MinecraftVersionType.Release,
                "https://piston-meta.mojang.com/v1/packages/6607feafdb2f96baad9314f207277730421a8e76/1.19.3.json",
                Instant.parse("2022-09-13T14:29:56+00:00"),
                Instant.parse("2022-09-13T14:29:56+00:00"),
                "6607feafdb2f96baad9314f207277730421a8e76",
                1
            ),
            0,
            0,
            Visibility.Unlisted,
            "asdawd",
            listOf(),
            Loader.Fabric,
            listOf(),
            listOf(),
            listOf(),
            listOf()
        ))
    }

    print(createResponse)
}