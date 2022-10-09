package dev.nyon.headquarters.app.profile.models

import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable

@Serializable
sealed interface Profile {
    @Serializable
    data class ModProfile(
        val gameVersion: String,
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
    var name: String = ""
    var profileID: String = ""
    var sharedProfileID: String = ""

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