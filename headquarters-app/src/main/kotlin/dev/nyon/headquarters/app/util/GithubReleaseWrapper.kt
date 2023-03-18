package dev.nyon.headquarters.app.util

import dev.nyon.headquarters.app.ktorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

suspend fun requestReleases(repo: String): List<Release> =
    ktorClient.request("https://api.github.com/repos/$repo/releases") {
        header("Accept", "application/vnd.github+json")
    }.body()

@Serializable
data class Release(
    val url: String,
    val html_url: String,
    val assets_url: String,
    val upload_url: String,
    val tarball_url: String,
    val zipball_url: String,
    val id: Int,
    val node_id: String,
    val tag_name: String,
    val target_commitish: String,
    val name: String,
    val body: String,
    val draft: Boolean,
    val prerelease: Boolean,
    val created_at: Instant,
    val published_at: Instant,
    val author: Map<String, JsonPrimitive>,
    val assets: List<Asset>
)

@Serializable
data class Asset(
    val url: String,
    val browser_download_url: String,
    val id: Int,
    val node_id: String,
    val name: String,
    val label: String?,
    val state: String,
    val content_type: String,
    val size: Int,
    val download_count: Int,
    val created_at: Instant,
    val updated_at: Instant,
    val uploader: Map<String, JsonPrimitive>
)