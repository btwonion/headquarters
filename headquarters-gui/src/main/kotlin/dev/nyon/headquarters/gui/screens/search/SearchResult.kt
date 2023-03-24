package dev.nyon.headquarters.gui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.connector.modrinth.models.result.SearchResult
import dev.nyon.headquarters.gui.screens.search.popup.ProjectPopup

context(ColumnScope)
@Composable
fun SearchResultBox(
    theme: ColorScheme,
    profile: Profile?,
    searchResponse: SearchResult,
    gridState: LazyGridState,
    searchResults: SnapshotStateList<ProjectResult>,
    showPopup: Boolean,
    selectedProject: ProjectResult?,
    showPopupCallback: Boolean.() -> Unit,
    selectedProjectCallback: ProjectResult?.() -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        when (searchResponse) {
            // Displays error
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
                        searchResponse.error,
                        Modifier.align(Alignment.CenterHorizontally).padding(10.dp),
                        fontSize = 25.sp
                    )
                    Text(
                        searchResponse.description,
                        Modifier.align(Alignment.CenterHorizontally).padding(10.dp),
                        fontSize = 20.sp
                    )
                }
            }

            // Lists all search results
            is SearchResult.SearchResultSuccess -> {
                // Prints error if no project could be found
                if (searchResponse.totalHits <= 0) {
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
                                fontWeight = FontWeight.Bold
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
                    items(searchResults) {
                        ProjectItem(it) {
                            selectedProjectCallback(it)
                            showPopupCallback(true)
                        }
                    }
                }
            }
        }


        // Open ProjectPopup
        if (showPopup && selectedProject != null) {
            ProjectPopup(selectedProject, appScope, profile) {
                showPopupCallback(false)
                selectedProjectCallback(null)
            }
        }
    }
}