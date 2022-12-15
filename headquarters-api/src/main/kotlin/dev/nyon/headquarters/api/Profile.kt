package dev.nyon.headquarters.api

import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Profile {
    @Serializable
    data class ModProfile(
        var id: String,
        var owner: String,
        @SerialName("minecraft_version") var minecraftVersion: MinecraftVersion,
        var loader: Loader,
        @SerialName("jvm_args") var jvmArgs: List<String>,
        var configs: List<Config>,
        @SerialName("extra_mods") var extraMods: List<ProjectEntry>,
        var templates: List<ModProfile>,
        var followers: Int,
        var downloads: Int,
        var categories: List<Category>,
        var visibility: Visibility,
        var name: String
    ) : Profile

    @Serializable
    data class ResourcePackProfile(
        @SerialName("game_version") var gameVersion: String,
        @SerialName("resource_packs") var resourcePacks: List<ProjectEntry>,
        var id: String
    ) : Profile
}


@Serializable
enum class Visibility {
    @SerialName("private")
    Private,

    @SerialName("public")
    Public,

    @SerialName("discoverable")
    Discoverable
}

val Profile.ModProfile.mods: List<ProjectEntry>
    get() = mutableListOf<ProjectEntry>().also { list ->
        list.addAll(templates.flatMap { it.mods })
        list.addAll(extraMods)
    }