package dev.nyon.headquarters.app.profile

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.mojangConnector
import dev.nyon.headquarters.app.quiltConnector
import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.app.util.fabricProfile
import dev.nyon.headquarters.app.util.generateID
import dev.nyon.headquarters.connector.modrinth.models.project.ProjectType
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.request.Facet
import dev.nyon.headquarters.connector.mojang.models.MinecraftVersionType
import dev.nyon.headquarters.connector.quilt.requests.getLoaderProfile
import dev.nyon.headquarters.connector.quilt.requests.getLoadersOfGameVersion
import kotlinx.coroutines.launch

suspend fun createDefaultProfile() {
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
    appScope.launch {
        profile.init()
    }
    profileDB.write {
        copyToRealm(profile)
    }
}

fun Profile.eventuallySupportedVersions(): List<String> = when {
    minecraftVersion.type == MinecraftVersionType.Release && minecraftVersionID.count { it == '.' } == 2 -> {
        val list = mutableListOf<String>()
        val majorVersion = minecraftVersionID.dropLastWhile { it != '.' }.dropLast(1)
        list.add(majorVersion)
        val currentMinorVersion = minecraftVersionID.last { it != '.' }.digitToIntOrNull()
        if (currentMinorVersion != null) (1..currentMinorVersion).forEach {
            list.add("$majorVersion.$it")
        }
        list
    }

    minecraftVersion.type == MinecraftVersionType.Snapshot
            && minecraftVersionID.contains("-rc") || minecraftVersionID.contains("-pre")
    -> {
        val lastLetter = if (minecraftVersionID.contains("-rc")) 'c' else 'e'
        val list = mutableListOf<String>()
        val releaseTypeString = minecraftVersionID.dropLastWhile { it != lastLetter }
        val minorRcVersion = minecraftVersionID.last { it != lastLetter }.digitToIntOrNull()
        if (minorRcVersion != null) (1..minorRcVersion).forEach {
            list.add("$releaseTypeString$it")
        } else list.add(minecraftVersionID)
        list
    }

    else -> listOf(minecraftVersionID)
}

fun Profile?.generateFacets(): List<Facet<*>> =
    mutableListOf<Facet<*>>(Facet.ProjectType(listOf(ProjectType.Mod))).also {
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