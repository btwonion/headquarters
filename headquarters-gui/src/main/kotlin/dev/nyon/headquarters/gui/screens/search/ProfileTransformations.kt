package dev.nyon.headquarters.gui.screens.search

import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.database.updateProfile
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.profile.Project
import dev.nyon.headquarters.app.profile.assureModExists
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import kotlinx.coroutines.launch

fun Profile.addMod(version: Version) {
    val modProject =
        Project(projectID = version.projectID, versionID = version.id).apply {
            projectID = version.projectID
            versionID = version.id
            enabled = true
        }
    appScope.launch {
        launch {
            assureModExists(modProject)
        }
        updateProfile(profileID) {
            this.mods.add(modProject)
        }
    }
}