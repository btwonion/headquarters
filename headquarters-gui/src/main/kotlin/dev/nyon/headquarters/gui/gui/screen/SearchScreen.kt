package dev.nyon.headquarters.gui.gui.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.MoodSad
import dev.nyon.headquarters.app.connector
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.connector.modrinth.models.result.SearchResult
import dev.nyon.headquarters.connector.modrinth.requests.searchProjects
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*
import kotlinx.coroutines.*

@Composable
fun SearchScreen() {
    var searchResponse by remember {
        mutableStateOf<SearchResult>(
            SearchResult.SearchResultFailure(
                "inital", "This is just an initial value"
            )
        )
    }
    var hits by remember { mutableStateOf(arrayListOf<ProjectResult>()) }
    var currentInput by remember { mutableStateOf("") }
    val searchScope = rememberCoroutineScope()

    fun search(typing: Boolean) {
        val term = currentInput

        searchScope.launch {
            if (typing) {
                delay(200)
                if (currentInput != term) return@launch
            }

            val offset =
                if (searchResponse is SearchResult.SearchResultSuccess) (searchResponse as SearchResult.SearchResultSuccess).totalHits + (searchResponse as SearchResult.SearchResultSuccess).offset else 0
            val result = connector.searchProjects(term, limit = 25, offset = if (term == currentInput) offset else null)
            searchResponse = result
            if (result !is SearchResult.SearchResultSuccess) return@launch
            if (term == currentInput) hits += result.hits
            else hits = result.hits as ArrayList<ProjectResult>
        }
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

        val gridState = rememberLazyGridState()

        Box {
            when (val response = searchResponse) {
                is SearchResult.SearchResultFailure -> {
                    Column {
                        Image(
                            TablerIcons.MoodSad, "error", alignment = Alignment.Center, contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(50.dp))
                        Text(response.error, Modifier.padding(10.dp), fontSize = 18.sp)
                        Text(response.description, Modifier.padding(10.dp), fontSize = 12.sp)
                    }
                }

                is SearchResult.SearchResultSuccess -> {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(500.dp),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(hits) {
                            ProjectItem(it)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectItem(project: ProjectResult) {
    ElevatedCard(modifier = Modifier.height(135.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            KamelImage(
                lazyPainterResource(data = Url(project.iconUrl ?: "https://cdn-raw.modrinth.com//placeholder.svg")),
                project.title,
                modifier = Modifier.size(115.dp).padding(top = 20.dp, start = 10.dp).clip(RoundedCornerShape(8.dp))
                    .aspectRatio(1f),
                alignment = Alignment.Center
            )
            Spacer(Modifier.fillMaxHeight().width(10.dp))
            Column {
                Spacer(Modifier.height(5.dp))
                Text(project.title)
                Text(project.author)
                Spacer(Modifier.size(10.dp))
                Text(project.description)
            }
        }
    }
}