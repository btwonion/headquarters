package dev.nyon.headquarters.app.profile.models

import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable

@Serializable
sealed interface Profile {
    @Serializable
    data class ModProfile(
        val version: MinecraftVersion,
        val loader: Loader,
        val jvmArgs: List<String>,
        val configs: List<Config>,
        val mods: List<ProjectEntry>,
        val id: String
    ) : Profile

    @Serializable
    data class ResourcePackProfile(val gameVersion: String, val resourcePacks: List<ProjectEntry>, val id: String) :
        Profile
}

class LocalProfile() : RealmObject {
    var name = ""
    var profileID = ""
    var sharedProfileID = ""

    constructor(name: String, profileID: String, sharedProfileID: String) : this() {
        this.name = name
        this.profileID = profileID
        this.sharedProfileID = sharedProfileID
    }
}

@Serializable
data class SharedProfile(
    val owner: String,
    val profileID: String,
    val id: String,
    val followers: Int,
    val downloads: Int,
    val name: String,
    val categories: List<Category>
)