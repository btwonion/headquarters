package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import io.realm.kotlin.types.RealmObject
import java.nio.file.Path

class Profile() : RealmObject {
    var name = ""
    var profileID = ""
    var loader: Loader = Loader.Fabric
    lateinit var profileDir: Path
    lateinit var minecraftVersion: MinecraftVersion
    var modPack: Project? = null
    var memory: Int = 4
    var mods: MutableList<Project> = mutableListOf()
    var resourcePacks: MutableList<Project> = mutableListOf()

    constructor(
        name: String,
        profileID: String,
        profileDir: Path,
        loader: Loader,
        minecraftVersion: MinecraftVersion
    ) : this() {
        this.name = name
        this.profileID = profileID
        this.loader = loader
        this.minecraftVersion = minecraftVersion
        this.profileDir = profileDir
    }
}