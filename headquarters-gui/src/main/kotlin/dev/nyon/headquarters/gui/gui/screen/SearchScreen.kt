package dev.nyon.headquarters.gui.gui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import compose.icons.feathericons.Heart
import dev.nyon.headquarters.app.connector
import dev.nyon.headquarters.connector.modrinth.models.project.Project
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.connector.modrinth.models.result.SearchResult
import dev.nyon.headquarters.connector.modrinth.requests.getProject
import dev.nyon.headquarters.connector.modrinth.requests.searchProjects
import dev.nyon.headquarters.gui.util.toPrettyString
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.coroutines.*

context(BoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(theme: ColorScheme) {
    var searchResponse by remember { mutableStateOf<SearchResult>(SearchResult.SearchResultSuccess(listOf(), 0, 0, 0)) }
    val currentItems = remember { mutableStateListOf<ProjectResult>() }
    var currentInput by remember { mutableStateOf("") }
    val searchScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()

    fun search(typing: Boolean) {
        val term = currentInput

        searchScope.launch {
            if (typing) {
                delay(200)
                if (currentInput != term) return@launch
            }

            val result = connector.searchProjects(term, limit = 25)
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
                            ProjectItem(it, theme)
                        }
                    }
                }
            }
        }
    }
}

context(LazyGridItemScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectItem(project: ProjectResult, theme: ColorScheme) {
    var openedDialog by remember { mutableStateOf(false) }

    if (openedDialog) ProjectDialog(project, theme) { openedDialog = false }
    ElevatedCard({ openedDialog = true }, modifier = Modifier.height(135.dp)) {
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
fun ProjectDialog(projectResult: ProjectResult, theme: ColorScheme, onClose: () -> Unit) {
    var opened by remember { mutableStateOf(true) }
    val projectScope = rememberCoroutineScope()
    var project: Project? = null
    projectScope.launch {
        project = connector.getProject(projectResult.projectID)
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