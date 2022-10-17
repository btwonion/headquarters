package dev.nyon.headquarters.gui.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.Markdown
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.connector
import dev.nyon.headquarters.connector.modrinth.models.project.Project
import dev.nyon.headquarters.connector.modrinth.models.project.version.Version
import dev.nyon.headquarters.connector.modrinth.models.request.Facet
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.connector.modrinth.models.result.SearchResult
import dev.nyon.headquarters.connector.modrinth.requests.getProject
import dev.nyon.headquarters.connector.modrinth.requests.getVersion
import dev.nyon.headquarters.connector.modrinth.requests.getVersions
import dev.nyon.headquarters.connector.modrinth.requests.searchProjects
import dev.nyon.headquarters.gui.util.color
import dev.nyon.headquarters.gui.util.distance
import dev.nyon.headquarters.gui.util.toPrettyString
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import java.awt.Desktop
import java.net.URI
import java.text.NumberFormat

context(BoxScope)
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
fun SearchScreen(theme: ColorScheme) {
    var searchResponse by remember {
        mutableStateOf<SearchResult>(
            SearchResult.SearchResultSuccess(
                listOf(), 0, 0, 0
            )
        )
    }
    val currentItems = remember { mutableStateListOf<ProjectResult>() }
    var currentInput by remember { mutableStateOf("") }
    val searchScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    var selectedProject by remember { mutableStateOf<ProjectResult?>(null) }
    var showPopup by remember { mutableStateOf(false) }

    fun search(typing: Boolean) {
        val term = currentInput

        searchScope.launch {
            if (typing) {
                delay(200)
                if (currentInput != term) return@launch
            }

            val result = connector.searchProjects(term, limit = 25, facets = listOf(Facet.Categories(listOf("fabric"))))
            searchResponse = result
            if (result is SearchResult.SearchResultSuccess) {
                currentItems.clear()
                currentItems.addAll(result.hits)
            }
            if (term != currentInput) gridState.animateScrollToItem(1)
        }
    }

    gridState.onReachEnd {
        val result = connector.searchProjects(currentInput, offset = it, limit = 20)
        if (result is SearchResult.SearchResultSuccess) currentItems.addAll(result.hits)
        searchResponse = result
    }

    Box {
        Column {
            Box(Modifier.fillMaxWidth()) {
                TextField(
                    currentInput, {
                        currentInput = it
                        search(true)
                    }, modifier = Modifier.fillMaxWidth().padding(10.dp).align(Alignment.TopCenter), maxLines = 1
                )
            }

            Box(Modifier.fillMaxSize()) {
                when (val response = searchResponse) {
                    is SearchResult.SearchResultFailure -> {
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(theme.errorContainer, theme.error),
                            modifier = Modifier.size(350.dp, 500.dp).align(Alignment.TopCenter).padding(top = 100.dp)
                        ) {
                            Text(
                                "Error",
                                Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(50.dp),
                                fontSize = 30.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(50.dp))

                            Text(
                                response.error,
                                Modifier.align(Alignment.CenterHorizontally).padding(10.dp),
                                fontSize = 25.sp
                            )
                            Text(
                                response.description,
                                Modifier.align(Alignment.CenterHorizontally).padding(10.dp),
                                fontSize = 20.sp
                            )
                        }
                    }

                    is SearchResult.SearchResultSuccess -> {
                        if (response.totalHits <= 0) {
                            ElevatedCard(
                                colors = CardDefaults.elevatedCardColors(theme.primaryContainer, theme.primary),
                                modifier = Modifier.size(400.dp, 100.dp).align(Alignment.Center)
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    Text(
                                        "No projects found",
                                        Modifier.align(Alignment.Center),
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        color = theme.primary
                                    )
                                }
                            }
                        }

                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(500.dp),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(currentItems) {
                                ProjectItem(it) {
                                    showPopup = true
                                    selectedProject = it
                                }
                            }
                        }
                    }
                }


                if (showPopup && selectedProject != null) {
                    ProjectPage(selectedProject, searchScope) {
                        showPopup = false
                        selectedProject = null
                    }
                }
            }
        }
    }
}

context(ColumnScope)
        @Composable
        private fun ProjectSpec(icon: ImageVector, text: String, link: String) {
    val annotatedLinkString = buildAnnotatedString {
        append(text)

        addStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline
            ), start = 0, end = text.length
        )

        addStringAnnotation(
            tag = "URL", annotation = link, 0, text.length
        )
    }

    Row(Modifier.padding(start = 10.dp, top = 10.dp)) {
        Icon(icon, text.lowercase().replace(" ", "_"))
        Spacer(Modifier.width(5.dp))
        ClickableText(
            text = annotatedLinkString, modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            annotatedLinkString.getStringAnnotations("URL", it, it).firstOrNull()?.let { stringAnnotation ->
                Desktop.getDesktop().browse(URI(stringAnnotation.item))
            }
        }
    }
}

context(LazyGridItemScope)
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
fun ProjectItem(project: ProjectResult, onClick: () -> Unit) {
    ElevatedCard(onClick, modifier = Modifier.height(135.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            KamelImage(
                lazyPainterResource(data = Url(project.iconUrl ?: "https://cdn-raw.modrinth.com//placeholder.svg")),
                project.title,
                modifier = Modifier.size(115.dp).padding(top = 20.dp, start = 10.dp).clip(RoundedCornerShape(8.dp))
                    .aspectRatio(1f),
                alignment = Alignment.Center
            )
            Spacer(Modifier.fillMaxHeight().width(10.dp))
            Column(modifier = Modifier.fillMaxHeight()) {
                Column {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        project.title, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 18.sp
                    )
                    Spacer(Modifier.width(300.dp))
                    Text(project.author, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.size(10.dp))
                    Text(
                        project.description,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                }
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    Row(Modifier.fillMaxWidth().height(50.dp), horizontalArrangement = Arrangement.End) {
                        Row(Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Download, "downloads_${project.title}", Modifier.padding(end = 5.dp))
                            Text(
                                project.downloads.toPrettyString(), fontFamily = FontFamily.Monospace, fontSize = 15.sp
                            )
                        }
                        Row(Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Heart, "followers_${project.follows}", Modifier.padding(end = 5.dp))
                            Text(project.follows.toPrettyString(), fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyGridState.onReachEnd(buffer: Int = 8, onReachEnd: suspend (total: Int) -> Unit) =
    layoutInfo.onReachEnd(buffer, onReachEnd)

@Composable
private fun LazyGridLayoutInfo.onReachEnd(buffer: Int = 5, onReachEnd: suspend (total: Int) -> Unit) {
    val hasReachedEnd = remember(this) {
        val lastVisibleItemIndex = visibleItemsInfo.lastOrNull()?.index ?: 0
        (lastVisibleItemIndex + 1 > (totalItemsCount - buffer)) to totalItemsCount
    }

    LaunchedEffect(hasReachedEnd) {
        if (hasReachedEnd.first) onReachEnd(totalItemsCount)
    }
}


context(BoxScope)
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        private fun ProjectPage(selectedProject: ProjectResult?, searchScope: CoroutineScope, onClose: () -> Unit) {
    var project by remember { mutableStateOf<Project?>(null) }
    var latestVersion by remember { mutableStateOf<Version?>(null) }
    searchScope.launch {
        project = connector.getProject(selectedProject!!.slug)
        latestVersion = connector.getVersion(project!!.versions.last())
    }

    ElevatedCard(Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp)) {
        if (project == null || latestVersion == null) Box(Modifier.fillMaxSize()) {
            Text("Loading...", Modifier.align(Alignment.Center), fontSize = 20.sp)
        } else Column {
            Box(Modifier.fillMaxWidth()) {
                IconButton({
                    onClose()
                }, Modifier.align(Alignment.CenterEnd).padding(5.dp)) {
                    Icon(FeatherIcons.X, "exit screen")
                }

                Button({}, Modifier.align(Alignment.CenterStart).padding(5.dp)) {
                    Icon(FeatherIcons.DownloadCloud, "install")
                    Text(
                        "Install", Modifier.padding(5.dp), fontWeight = FontWeight.Bold, color = Color.White
                    )
                }

                Divider(Modifier.align(Alignment.BottomCenter).padding(start = 5.dp, end = 5.dp))

                Text(
                    selectedProject!!.title,
                    Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Row {
                Column(Modifier.width(210.dp)) {
                    KamelImage(
                        lazyPainterResource(
                            data = Url(
                                selectedProject!!.iconUrl ?: "https://cdn-raw.modrinth.com//placeholder.svg"
                            )
                        ),
                        selectedProject.title,
                        modifier = Modifier.size(200.dp).padding(top = 20.dp, start = 10.dp)
                            .clip(RoundedCornerShape(8.dp)).aspectRatio(1f),
                        alignment = Alignment.Center
                    )

                    Text(
                        project!!.description, modifier = Modifier.padding(10.dp).padding(bottom = 0.dp)
                    )
                    Divider(Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp))

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

                    if (project?.sourceUrl != null) ProjectSpec(
                        FeatherIcons.Terminal, "Source Code", project!!.sourceUrl!!
                    )

                    if (project?.issuesUrl != null) ProjectSpec(
                        FeatherIcons.AlertTriangle, "Issue Tracker", project!!.issuesUrl!!
                    )

                    if (project?.discordUrl != null) ProjectSpec(
                        FeatherIcons.Share2, "Discord", project!!.discordUrl!!
                    )

                    if (project?.donationUrl != null) project?.donationUrl?.forEach {
                        ProjectSpec(FeatherIcons.Gift, it.platform, it.url)
                    }

                    if (project?.wikiUrl != null) ProjectSpec(
                        FeatherIcons.BookOpen, "Wiki", project!!.wikiUrl!!
                    )
                    Divider(Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp))

                    Row(Modifier.padding(top = 20.dp, start = 10.dp)) {
                        Icon(FeatherIcons.RefreshCw, "updated")
                        Spacer(Modifier.width(5.dp))
                        Text(
                            (project!!.updated - Clock.System.now()).distance(),
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    Row(Modifier.padding(top = 20.dp, start = 10.dp)) {
                        Icon(FeatherIcons.UploadCloud, "created")
                        Spacer(Modifier.width(5.dp))
                        Text(
                            (project!!.published - Clock.System.now()).distance(),
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Divider(Modifier.padding(start = 10.dp, top = 20.dp, end = 10.dp))

                    Text(
                        "Project ID: ${project!!.id}",
                        Modifier.padding(top = 20.dp, start = 10.dp),
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        "Client Side: ${project!!.clientSide.name}",
                        Modifier.padding(top = 5.dp, start = 10.dp),
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        "Server Side: ${project!!.serverSide.name}",
                        Modifier.padding(top = 5.dp, start = 10.dp),
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        "License: ${project!!.license.id.uppercase()}",
                        Modifier.padding(top = 5.dp, start = 10.dp),
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        "Loaders: ${latestVersion!!.loaders.joinToString { it.name }}",
                        Modifier.padding(top = 5.dp, start = 10.dp),
                        fontStyle = FontStyle.Italic
                    )
                }

                Column(Modifier.fillMaxSize()) {
                    var currentPage by remember { mutableStateOf(ProjectViewScreen.Overview) }

                    Row(Modifier.fillMaxWidth().padding(end = 210.dp), horizontalArrangement = Arrangement.Center) {
                        ProjectViewScreen.values().forEach {
                            if (it == ProjectViewScreen.Gallery && project!!.gallery.isEmpty()) return@forEach
                            FilledTonalButton(
                                { currentPage = it },
                                enabled = currentPage != it,
                                modifier = Modifier.padding(end = 5.dp)
                            ) {
                                Text(it.name)
                            }
                        }
                    }
                    Divider(Modifier.fillMaxWidth().padding(end = 80.dp, start = 70.dp))

                    when (currentPage) {
                        ProjectViewScreen.Overview -> {
                            LazyColumn(Modifier.fillMaxSize()) {
                                item {
                                    Text(
                                        project!!.title,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 5.dp)
                                    )
                                    Text(
                                        "by ${selectedProject!!.author}",
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

                        ProjectViewScreen.Gallery -> {
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

                        ProjectViewScreen.Versions -> {
                            val gridState = rememberLazyGridState()
                            val versions = remember { mutableStateListOf<Version>() }
                            searchScope.launch {
                                versions.addAll(connector.getVersions(project!!.versions)!!.reversed())
                            }

                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Adaptive(800.dp),
                                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(15.dp),
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                items(versions) {
                                    Card(Modifier.padding(5.dp)) {
                                        Row {
                                            Text(
                                                it.versionType.name,
                                                color = it.versionType.color,
                                                modifier = Modifier.padding(5.dp)
                                            )
                                            Text(
                                                "${it.name}  - ${it.id}",
                                                modifier = Modifier.padding(5.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                (it.published - Clock.System.now()).distance(),
                                                Modifier.align(Alignment.CenterVertically).padding(start = 10.dp)
                                            )
                                        }
                                        Text(
                                            "${it.loaders.joinToString { it.name }} - ${it.gameVersions.joinToString()}",
                                            Modifier.padding(5.dp)
                                        )
                                        if (it.changelog != null) Markdown(
                                            it.changelog!!, modifier = Modifier.padding(5.dp).padding(top = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class ProjectViewScreen {
    Overview, Gallery, Versions
}