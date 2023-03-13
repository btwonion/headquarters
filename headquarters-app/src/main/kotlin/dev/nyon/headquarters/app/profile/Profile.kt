package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import io.realm.kotlin.types.RealmObject
import java.nio.file.Path

class Profile() : RealmObject {
    var name: String = ""
    var profileID: String = ""
    var loader: Loader = Loader.Fabric
    lateinit var loaderProfile: LoaderProfile
    var profileDir: Path = runningDir
    lateinit var minecraftVersion: VersionPackage
    var modPack: Project? = null
    var memory: Int = 4
    var mods: MutableList<Project> = mutableListOf()
    var resourcePacks: MutableList<Project> = mutableListOf()
}