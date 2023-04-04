package dev.nyon.headquarters.gui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import compose.icons.feathericons.Heart
import dev.nyon.headquarters.connector.modrinth.models.result.ProjectResult
import dev.nyon.headquarters.gui.util.toPrettyString
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*

context(LazyGridItemScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectItem(
    project: ProjectResult,
    theme: ColorScheme,
    enabled: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick,
        modifier = Modifier.height(135.dp),
        enabled = enabled,
        colors = CardDefaults.elevatedCardColors(theme.primaryContainer, theme.onPrimaryContainer)
    ) {
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