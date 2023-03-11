package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersion
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import io.realm.kotlin.types.RealmObject
import kotlinx.datetime.Clock
import java.nio.file.Path

val testMinecraftVersion = MinecraftVersion(
    "1.19.3",
    MinecraftVersionType.Release,
    "https://piston-meta.mojang.com/v1/packages/7c7a49009bf7d62324226b3536e046e0dbbc8141/1.19.3.json",
    Clock.System.now(),
    Clock.System.now(),
    "7c7a49009bf7d62324226b3536e046e0dbbc8141",
    1
)

class Profile() : RealmObject {
    var name: String = ""
    var profileID: String = ""
    var loader: Loader = Loader.Fabric
    var profileDir: Path = runningDir
    var minecraftVersion: MinecraftVersion = testMinecraftVersion
    var modPack: Project? = null
    var memory: Int = 4
    var mods: MutableList<Project> = mutableListOf()
    var resourcePacks: MutableList<Project> = mutableListOf()
}