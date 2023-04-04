package dev.nyon.headquarters.gui.screens.search.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.DownloadCloud
import compose.icons.feathericons.X
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.connector.modrinth.models.project.Project
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.gui.screens.search.addMod

context(ColumnScope)
@Composable
fun ProjectViewTopBar(
    profile: Profile?,
    versions: SnapshotStateList<Version>,
    project: Project?,
    selectedProject: ProjectResult?,
    theme: ColorScheme,
    onClose: () -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        // Add exit button
        IconButton(
            { onClose() },
            Modifier.align(Alignment.CenterEnd).padding(5.dp),
            colors = IconButtonDefaults.iconButtonColors(theme.secondaryContainer, theme.onSecondaryContainer)
        ) {
            Icon(FeatherIcons.X, "exit screen")
        }

        // Adds overall install button
        Button(
            {
                val firstVersion =
                    versions.find { it.versionType == profile!!.defaultProjectReleaseType } ?: versions.first()
                profile!!.addMod(firstVersion)
            },
            enabled = profile != null && profile.mods.none { mod -> mod.projectID == project!!.id } && versions.isNotEmpty(),
            modifier = Modifier.align(Alignment.CenterStart).padding(5.dp),
            colors = ButtonDefaults.buttonColors(theme.secondary, theme.onSecondary)
        ) {
            Icon(FeatherIcons.DownloadCloud, "install")
            Text("Install", Modifier.padding(5.dp), fontWeight = FontWeight.Bold)
        }

        Divider(
            Modifier.align(Alignment.BottomCenter).padding(start = 5.dp, end = 5.dp),
            color = theme.onSecondaryContainer
        )

        // Adds project's title as header
        Text(
            selectedProject!!.title,
            Modifier.align(Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = theme.onSecondaryContainer
        )
    }
}