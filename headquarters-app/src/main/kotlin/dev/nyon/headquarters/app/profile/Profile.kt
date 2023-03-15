package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.fabricConnector
import dev.nyon.headquarters.app.mojangConnector
import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.connector.fabric.models.Arguments
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.fabric.requests.getLoaderProfile
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import dev.nyon.headquarters.connector.mojang.models.`package`.*
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import kotlinx.datetime.Instant
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class Profile() : RealmObject {
    var name: String = ""
    var profileID: String = ""
    var loaderName = ""
    var loader: Loader
        get() = Loader.valueOf(loaderName)
        set(value) {
            loaderName = value.name
        }
    var profileDirString: String = runningDir.absolutePathString()
    var profileDir: Path
        get() = Path.of(profileDirString)
        set(value) {
            profileDirString = value.absolutePathString()
        }
    var minecraftVersionID: String = ""

    @Ignore
    var minecraftVersion: VersionPackage = emptyVersionPackage
        set(value) {
            minecraftVersionID = value.id
            field = value
        }
    var loaderVersion = ""

    @Ignore
    var loaderProfile: LoaderProfile = emptyLoaderProfile
    var modPack: Project? = null
    var memory: Int = 4
    var mods: RealmList<Project> = realmListOf()
    var resourcePacks: RealmList<Project> = realmListOf()
    suspend fun initMinecraftVersionPackage() {
        minecraftVersion = mojangConnector.getVersionPackage(minecraftVersionID)
            ?: error("VersionPackage for version '$minecraftVersionID' cannot be found!")
    }

    suspend fun initLoaderProfile() {
        loaderProfile = fabricConnector.getLoaderProfile(loaderVersion, minecraftVersionID)
            ?: error("LoaderProfile for fabric loader version '$loaderVersion' and minecraft version '$minecraftVersionID' cannot be found!")
    }
}

private val emptyVersionPackage = VersionPackage(
    PackageArguments(listOf()),
    AssetIndex("", "", 1, 1, ""),
    "",
    1,
    Downloads(
        DownloadEntry("", 1, ""),
        DownloadEntry("", 1, ""),
        DownloadEntry("", 1, ""),
        DownloadEntry("", 1, "")
    ),
    "",
    JavaVersion("", 1),
    listOf(),
    Logging(),
    "",
    1,
    Instant.DISTANT_FUTURE,
    Instant.DISTANT_FUTURE,
    MinecraftVersionType.Release
)

private val emptyLoaderProfile = LoaderProfile("", "", "", "", "", "", Arguments(listOf()), listOf())