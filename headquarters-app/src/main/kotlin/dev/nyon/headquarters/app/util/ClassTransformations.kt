package dev.nyon.headquarters.app.util

import dev.nyon.headquarters.connector.fabric.models.Arguments
import dev.nyon.headquarters.connector.fabric.models.Artifact
import dev.nyon.headquarters.connector.fabric.models.LoaderProfile
import dev.nyon.headquarters.connector.quilt.models.Arguments as QuiltArguments
import dev.nyon.headquarters.connector.quilt.models.LoaderProfile as QuiltLoaderProfile

fun QuiltLoaderProfile.fabricProfile(): LoaderProfile = LoaderProfile(
    id,
    inheritsFrom,
    releaseTime,
    time,
    type,
    mainClass,
    arguments.fabricArguments(),
    libraries.map { Artifact(it.name, it.url) }
)

fun QuiltArguments.fabricArguments(): Arguments = Arguments(game, jvm)