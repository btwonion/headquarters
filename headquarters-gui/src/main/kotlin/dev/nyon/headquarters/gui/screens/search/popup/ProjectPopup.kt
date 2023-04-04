package dev.nyon.headquarters.gui.screens.search.popup

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.modrinthConnector
import dev.nyon.headquarters.app.profile.eventuallySupportedVersions
import dev.nyon.headquarters.connector.modrinth.models.project.Project
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.connector.modrinth.requests.getProject
import dev.nyon.headquarters.connector.modrinth.requests.listVersions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

context(BoxScope)
@Composable
fun ProjectPopup(
    selectedProject: ProjectResult,
    searchScope: CoroutineScope,
    profile: Profile?,
    theme: ColorScheme,
    exits: SnapshotStateList<() -> Unit>,
    onClose: () -> Unit
) {
    exits.add(onClose)
    var project by remember { mutableStateOf<Project?>(null) }
    val versions = remember { mutableStateListOf<Version>() }
    // Requests Modrinth models
    searchScope.launch {
        project = modrinthConnector.getProject(selectedProject.slug)
        versions.clear()
        versions.addAll(
            modrinthConnector.listVersions(
                selectedProject.projectID,
                loaders = if (profile == null) listOf() else mutableListOf(Loader.Fabric).also {
                    if (profile.loader == Loader.Quilt) it.add(Loader.Quilt)
                },
                if (profile?.minecraftVersion == null) listOf() else profile.eventuallySupportedVersions()
            )!!
        )
    }

    // Opens popup
    ElevatedCard(
        Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp),
        colors = CardDefaults.elevatedCardColors(theme.secondaryContainer, theme.onSecondaryContainer)
    ) {
        if (project == null) Box(Modifier.fillMaxSize()) {
            Text(
                "Loading...",
                Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                color = theme.onSecondaryContainer
            )
        } else Column {
            // Creates project top bar
            ProjectViewTopBar(profile, versions, project, selectedProject, theme, onClose)

            Row {
                // Creates project overview
                ProjectViewOverview(selectedProject, project, versions, theme)

                // Creates main project surface to display Overview/Gallery/Versions
                Column(Modifier.fillMaxSize()) {
                    var currentPage by remember { mutableStateOf(ProjectViewScreen.Overview) }

                    Row(Modifier.fillMaxWidth().padding(end = 210.dp), horizontalArrangement = Arrangement.Center) {
                        ProjectViewScreen.values().forEach {
                            if (it == ProjectViewScreen.Gallery && project!!.gallery.isEmpty()) return@forEach
                            FilledTonalButton(
                                { currentPage = it },
                                enabled = currentPage != it,
                                modifier = Modifier.padding(end = 5.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    theme.secondary,
                                    theme.onSecondary,
                                    theme.secondary.copy(alpha = 0.12f),
                                    theme.onSecondary.copy(alpha = 0.12f)
                                )
                            ) {
                                Text(it.name)
                            }
                        }
                    }
                    Divider(Modifier.fillMaxWidth().padding(end = 80.dp, start = 70.dp))

                    when (currentPage) {
                        ProjectViewScreen.Overview -> OverviewProjectView(project, selectedProject, theme)
                        ProjectViewScreen.Gallery -> GalleryProjectView(project, theme)
                        ProjectViewScreen.Versions -> VersionsProjectView(profile, project, versions, theme)
                    }
                }
            }
        }
    }
}

enum class ProjectViewScreen {
    Overview, Gallery, Versions
}