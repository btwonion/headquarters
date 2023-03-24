package dev.nyon.headquarters.gui.screens.search

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.Project
import dev.nyon.headquarters.app.profile.assureModExists
import dev.nyon.headquarters.app.profile.updateProfile
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import kotlinx.coroutines.launch

fun Profile.addProject(version: Version) {
    val modProject =
        Project().apply {
            projectID = version.projectID
            versionID = version.id
            enabled = true
        }
    appScope.launch {
        launch {
            assureModExists(modProject)
        }
        updateProfile(profileID) {
            it.mods.add(modProject)
        }
    }
}