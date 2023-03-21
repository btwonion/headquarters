package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.fabricConnector
import dev.nyon.headquarters.app.mojangConnector
import dev.nyon.headquarters.app.quiltConnector
import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.app.util.fabricProfile
import dev.nyon.headquarters.app.util.generateID
import dev.nyon.headquarters.connector.fabric.models.Arguments
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.fabric.requests.getLoaderProfile
import dev.nyon.headquarters.connector.modrinth.models.project.ProjectType
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.project.version.VersionType
import dev.nyon.headquarters.connector.modrinth.models.request.Facet
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import dev.nyon.headquarters.connector.mojang.models.`package`.*
import dev.nyon.headquarters.connector.quilt.requests.getLoaderProfile
import dev.nyon.headquarters.connector.quilt.requests.getLoadersOfGameVersion
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
    var defaultModReleaseTypeName: String = VersionType.Release.name
    @Ignore
    var defaultModReleaseType: VersionType = VersionType.Release
        set(value) {
            defaultModReleaseTypeName = defaultModReleaseType.name
            field = value
        }
    var resourcePacks: RealmList<Project> = realmListOf()
    suspend fun initMinecraftVersionPackage() {
        minecraftVersion = mojangConnector.getVersionPackage(minecraftVersionID)
            ?: error("VersionPackage for version '$minecraftVersionID' cannot be found!")
    }

    suspend fun initLoaderProfile() {
        loaderProfile = if (loader == Loader.Quilt) quiltConnector.getLoaderProfile(loaderVersion, minecraftVersionID)
            ?.fabricProfile()
            ?: error("LoaderProfile for quilt loader version '$loaderVersion' and minecraft version '$minecraftVersionID' cannot be found!")
        else fabricConnector.getLoaderProfile(loaderVersion, minecraftVersionID)
            ?: error("LoaderProfile for fabric loader version '$loaderVersion' and minecraft version '$minecraftVersionID' cannot be found!")

    }
}

fun Profile?.generateFacets(): List<Facet<*>> = mutableListOf<Facet<*>>(Facet.ProjectType(listOf(ProjectType.Mod))).also {
    if (this != null) it.addAll(
        listOf(
            Facet.Categories(
                when (this.loader) {
                    Loader.Fabric -> mutableListOf("fabric")
                    Loader.Quilt -> mutableListOf("fabric", "quilt")
                    else -> mutableListOf()
                }
            ),
            Facet.Version(this.eventuallySupportedVersions())
        )
    )
}
suspend fun createNewProfile() {
    val profile =
        Profile().apply {
            name = "Profile 1"
            profileID = generateID()
            loader = Loader.Quilt
            minecraftVersion =
                mojangConnector.getVersionPackage(mojangConnector.getVersionManifest()!!.latest.release)!!

            val latestLoaderVersion = (quiltConnector.getLoadersOfGameVersion(
                minecraftVersion.id
            )?.first()
                ?: error("Cannot find compatible fabric loader for version '${minecraftVersion.id}'")).loader.version
            loaderVersion = latestLoaderVersion
            loaderProfile = quiltConnector.getLoaderProfile(
                latestLoaderVersion,
                minecraftVersion.id
            )?.fabricProfile()
                ?: error("Cannot find compatible fabric loader for version '${minecraftVersion.id}'")
            profileDir = runningDir.resolve("profiles/${name}_$profileID/")
        }
    profile.init()
    realm.write {
        copyToRealm(profile)
    }
}

fun Profile.eventuallySupportedVersions(): List<String> {
    if (minecraftVersion.type == MinecraftVersionType.Release && minecraftVersionID.count { it == '.' } == 2) {
        val list = mutableListOf<String>()
        val majorVersion = minecraftVersionID.dropLastWhile { it != '.' }.dropLast(1)
        list.add(majorVersion)
        val currentMinorVersion = minecraftVersionID.last { it != '.' }.digitToIntOrNull() ?: return list
        (1..currentMinorVersion).forEach {
            list.add("$majorVersion.$it")
        }
        return list
    }
    return listOf(minecraftVersionID)
}

private val emptyVersionPackage = VersionPackage(
    PackageArguments(listOf(), listOf()),
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