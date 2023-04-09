package dev.nyon.headquarters.app.database.models

import dev.nyon.headquarters.app.database.DatabaseEntry
import dev.nyon.headquarters.app.fabricConnector
import dev.nyon.headquarters.app.mojangConnector
import dev.nyon.headquarters.app.profile.Project
import dev.nyon.headquarters.app.quiltConnector
import dev.nyon.headquarters.app.util.PathSerializer
import dev.nyon.headquarters.app.util.fabricProfile
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.fabric.requests.getLoaderProfile
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.project.version.VersionType
import dev.nyon.headquarters.connector.mojang.models.`package`.VersionPackage
import dev.nyon.headquarters.connector.quilt.requests.getLoaderProfile
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path

@Serializable
data class Profile(
    override val version: Int = 1,
    var selected: Boolean,
    var name: String,
    var profileID: String,
    var loader: Loader,
    var profileDir: @Serializable(with = PathSerializer::class) Path,
    var minecraftVersionID: String,
    var loaderVersionID: String,
    var modPack: Project?,
    var memory: Int,
    var mods: MutableList<Project>,
    var resourcePacks: MutableList<Project>,
    var defaultProjectReleaseType: VersionType,
    var extraJvmArgs: MutableList<String>,
    var extraGameStartArgs: MutableList<String>
) : DatabaseEntry<Unit> {
    @Transient
    var minecraftVersion: VersionPackage? = null

    @Transient
    var loaderProfile: LoaderProfile? = null
    suspend fun initMinecraftVersionPackage() {
        minecraftVersion = mojangConnector.getVersionPackage(minecraftVersionID)
            ?: error("VersionPackage for version '$minecraftVersionID' cannot be found!")
    }

    suspend fun initLoaderProfile() {
        loaderProfile = if (loader == Loader.Quilt) quiltConnector.getLoaderProfile(loaderVersionID, minecraftVersionID)
            ?.fabricProfile()
            ?: error("LoaderProfile for quilt loader version '$loaderVersionID' and minecraft version '$minecraftVersionID' cannot be found!")
        else fabricConnector.getLoaderProfile(loaderVersionID, minecraftVersionID)
            ?: error("LoaderProfile for fabric loader version '$loaderVersionID' and minecraft version '$minecraftVersionID' cannot be found!")
    }

    override fun migrateToNewerEntry() {}
}