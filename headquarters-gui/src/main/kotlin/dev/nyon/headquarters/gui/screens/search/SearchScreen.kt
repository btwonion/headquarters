package dev.nyon.headquarters.gui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.modrinthConnector
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.generateFacets
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.connector.modrinth.models.result.SearchResult
import dev.nyon.headquarters.connector.modrinth.requests.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.*

context(BoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(theme: ColorScheme, profile: Profile?) {
    var searchResponse by remember {
        mutableStateOf<SearchResult>(
            SearchResult.SearchResultSuccess(
                listOf(), 0, 0, 0
            )
        )
    }
    val searchResults = remember { mutableStateListOf<ProjectResult>() }
    var input by remember { mutableStateOf("") }
    val searchScope = rememberCoroutineScope()
    val gridState = rememberLazyGridState()
    var selectedProject by remember { mutableStateOf<ProjectResult?>(null) }
    var showPopup by remember { mutableStateOf(false) }

    fun search(typing: Boolean) {
        val term = input

        searchScope.launch {
            if (typing) {
                delay(200)
                if (input != term) return@launch
            }

            val result =
                modrinthConnector.searchProjects(
                    term,
                    limit = 25,
                    facets = profile.generateFacets()
                )

            searchResponse = result
            if (result is SearchResult.SearchResultSuccess) {
                searchResults.clear()
                searchResults.addAll(result.hits)
            }
            if (term != input) gridState.animateScrollToItem(1)
        }
    }

    gridState.onReachEnd {
        val result = modrinthConnector.searchProjects(
            input, offset = it, limit = 20, facets = profile.generateFacets()
        )
        if (result is SearchResult.SearchResultSuccess) searchResults.addAll(result.hits)
        searchResponse = result
    }

    Box {
        Column {
            // Creates the input field
            Box(Modifier.fillMaxWidth()) {
                TextField(
                    input, {
                        input = it
                        search(true)
                    }, modifier = Modifier.fillMaxWidth().padding(10.dp).align(Alignment.TopCenter), maxLines = 1
                )
            }

            // Creates the search result surface
            SearchResultBox(
                profile,
                searchResponse,
                theme,
                gridState,
                searchResults,
                showPopup,
                selectedProject,
                { showPopup = this },
                { selectedProject = this })
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


