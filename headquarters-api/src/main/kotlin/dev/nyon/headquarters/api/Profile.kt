package dev.nyon.headquarters.api

import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Profile : IdHolder {
    override var id: String
    var visibility: Visibility
    var name: String
    var followers: Int
    var downloads: Int
    var owner: String

    @SerialName("minecraft_version")
    var minecraftVersion: MinecraftVersion

    @Serializable
    data class ModProfile(
        override var id: String,
        override var owner: String,
        override var minecraftVersion: MinecraftVersion,
        override var followers: Int,
        override var downloads: Int,
        override var visibility: Visibility,
        override var name: String,
        var categories: List<Category>,
        var loader: Loader,
        @SerialName("jvm_args") var jvmArgs: List<String>,
        var configs: List<Config>,
        @SerialName("extra_mods") var extraMods: List<ProjectEntry>,
        var templates: List<ModProfile>
    ) : Profile

    @Serializable
    data class ResourcePackProfile(
        override var minecraftVersion: MinecraftVersion,
        override var id: String,
        override var visibility: Visibility,
        override var name: String,
        override var downloads: Int,
        override var followers: Int,
        override var owner: String,
        @SerialName("resource_packs") var resourcePacks: List<ProjectEntry>
    ) : Profile
}


@Serializable
enum class Visibility {
    @SerialName("private")
    Private,

    @SerialName("public")
    Unlisted,

    @SerialName("discoverable")
    Public
}

val Profile.ModProfile.mods: List<ProjectEntry>
    get() = mutableListOf<ProjectEntry>().also { list ->
        list.addAll(templates.flatMap { it.mods })
        list.addAll(extraMods)
    }