package dev.nyon.headquarters.gui.screens.search.popup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.Markdown
import compose.icons.FeatherIcons
import compose.icons.feathericons.DownloadCloud
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.connector.modrinth.models.project.Project
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.gui.screens.search.addProject
import dev.nyon.headquarters.gui.util.color
import dev.nyon.headquarters.gui.util.distance
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.datetime.Clock

// Displays title/author and readme
context(ColumnScope)
@Composable
fun OverviewProjectView(project: Project?, selectedProject: ProjectResult) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text(
                project!!.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                "by ${selectedProject.author}",
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        item {
            Spacer(Modifier.height(50.dp))
            Markdown(project!!.body)
        }
    }
}

// Displays all project gallery images
context(ColumnScope)
@Composable
fun GalleryProjectView(project: Project?) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(project!!.gallery) {
            Column(Modifier.padding(top = 10.dp, bottom = 10.dp)) {
                KamelImage(
                    lazyPainterResource(Url(it.url)),
                    it.title,
                    Modifier.align(Alignment.Start).width(800.dp)
                )
                if (it.title != null) Text(
                    it.title!!, fontSize = 16.sp, textDecoration = TextDecoration.Underline
                )
                if (it.description != null) Text(it.description!!, fontSize = 14.sp)
            }
        }
    }
}

// Lists all project versions matching the profile's requirements
context(ColumnScope)
@Composable
fun VersionsProjectView(profile: Profile?, project: Project?, versions: SnapshotStateList<Version>) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(800.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(versions) {
            Card(Modifier.padding(5.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    // Displays if project is Release/Beta/Alpha
                    Text(
                        it.versionType.name,
                        color = it.versionType.color,
                        modifier = Modifier.padding(start = 5.dp)
                            .align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                    // Displays version name
                    Text(
                        it.name,
                        modifier = Modifier.padding(start = 10.dp)
                            .align(Alignment.CenterVertically),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    // Displays when the version was published
                    Text(
                        (it.published - Clock.System.now()).distance(),
                        Modifier.align(Alignment.CenterVertically).padding(start = 10.dp),
                        fontSize = 18.sp
                    )
                    // Displays id of the version
                    val annotatedLinkString = buildAnnotatedLinkString(
                        it.id,
                        "https://modrinth.com/mod/${it.projectID}/version/${it.id}",
                        FontStyle.Italic,
                        18.sp
                    )
                    LinkText(annotatedLinkString, Modifier.padding(start = 15.dp).align(Alignment.CenterVertically))
                    // Creates install button for specific version
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            { profile!!.addProject(it) },
                            Modifier.padding(5.dp),
                            enabled = profile != null && profile.mods.none { mod -> mod.projectID == project!!.id }) {
                            Icon(FeatherIcons.DownloadCloud, "install")
                            Text(
                                "Install",
                                Modifier.padding(5.dp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Displays loader and supported game versions
                Text(
                    "${it.loaders.joinToString { it.name }} - ${it.gameVersions.joinToString()}",
                    Modifier.padding(start = 5.dp),
                    fontSize = 16.sp
                )
                // Displays changelog for the specific version if existent
                if (it.changelog != null) Markdown(
                    it.changelog!!, modifier = Modifier.padding(5.dp).padding(top = 20.dp)
                )
            }
        }
    }
}