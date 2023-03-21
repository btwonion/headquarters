package dev.nyon.headquarters.gui.screens.search.popup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.connector.modrinth.models.project.Project
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.gui.util.distance
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.datetime.Clock
import java.text.NumberFormat

context(RowScope)
@Composable
fun ProjectViewOverview(selectedProject: ProjectResult, project: Project?, versions: SnapshotStateList<Version>) {
    Column(Modifier.width(210.dp)) {
        // Creates mod icon
        KamelImage(
            lazyPainterResource(
                data = Url(
                    selectedProject.iconUrl ?: "https://cdn-raw.modrinth.com//placeholder.svg"
                )
            ),
            selectedProject.title,
            modifier = Modifier.size(200.dp).padding(top = 20.dp, start = 10.dp)
                .clip(RoundedCornerShape(8.dp)).aspectRatio(1f),
            alignment = Alignment.Center
        )

        // Creates mod description
        Text(project?.description.toString(), modifier = Modifier.padding(10.dp).padding(bottom = 0.dp))
        Divider(Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp))

        // Creates overview with downloads and follows
        Row {
            Row(Modifier.padding(top = 20.dp, start = 10.dp)) {
                Icon(FeatherIcons.Download, "downloads", Modifier.padding(end = 5.dp))
                Text(
                    NumberFormat.getInstance().format(selectedProject.downloads).replace(",", ".")
                )
            }

            Row(Modifier.padding(top = 20.dp, start = 10.dp)) {
                Icon(FeatherIcons.Heart, "follows", Modifier.padding(end = 5.dp))
                Text(
                    NumberFormat.getInstance().format(selectedProject.follows).replace(",", ".")
                )
            }
        }
        Divider(Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp))

        // Creates multiple project specs if available
        if (project?.sourceUrl != null) ProjectSpec(
            FeatherIcons.Terminal, "Source Code", project.sourceUrl!!
        )

        if (project?.issuesUrl != null) ProjectSpec(
            FeatherIcons.AlertTriangle, "Issue Tracker", project.issuesUrl!!
        )

        if (project?.discordUrl != null) ProjectSpec(
            FeatherIcons.Share2, "Discord", project.discordUrl!!
        )

        if (project?.donationUrl != null) project.donationUrl?.forEach {
            ProjectSpec(FeatherIcons.Gift, it.platform, it.url)
        }

        if (project?.wikiUrl != null) ProjectSpec(
            FeatherIcons.BookOpen, "Wiki", project.wikiUrl!!
        )
        Divider(Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp))

        // Displays when the mod was updated the last time
        Row(Modifier.padding(top = 20.dp, start = 10.dp)) {
            Icon(FeatherIcons.RefreshCw, "updated")
            Spacer(Modifier.width(5.dp))
            Text(
                (project!!.updated - Clock.System.now()).distance(),
                Modifier.align(Alignment.CenterVertically)
            )
        }

        // Displays when the mod was created
        Row(Modifier.padding(top = 20.dp, start = 10.dp)) {
            Icon(FeatherIcons.UploadCloud, "created")
            Spacer(Modifier.width(5.dp))
            Text(
                (project!!.published - Clock.System.now()).distance(),
                Modifier.align(Alignment.CenterVertically)
            )
        }
        Divider(Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp))

        // Displays multiple attributes of the project and redirect to the modrinth page on click
        val annotatedLinkString =
            buildAnnotatedLinkString("Project ID: ${project?.id}", "https://modrinth.com/mod/${project?.id}")
        LinkText(annotatedLinkString, Modifier.padding(top = 20.dp, start = 10.dp))
        Text(
            "Client Side: ${project?.clientSide?.name}",
            Modifier.padding(top = 5.dp, start = 10.dp),
            fontStyle = FontStyle.Italic
        )
        Text(
            "Server Side: ${project?.serverSide?.name}",
            Modifier.padding(top = 5.dp, start = 10.dp),
            fontStyle = FontStyle.Italic
        )
        Text(
            "License: ${project?.license?.id?.uppercase()}",
            Modifier.padding(top = 5.dp, start = 10.dp),
            fontStyle = FontStyle.Italic
        )
        Text(
            "Loaders: ${versions.firstOrNull()?.loaders?.joinToString { it.name }}",
            Modifier.padding(top = 5.dp, start = 10.dp),
            fontStyle = FontStyle.Italic
        )
    }
}