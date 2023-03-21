package dev.nyon.headquarters.gui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.core.Resource
import io.kamel.image.KamelImage

context(BoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsCard(
    onClick: () -> Unit,
    modifier: Modifier,
    title: String,
    subtitle: String,
    previewText: String,
    image: Resource<Painter>? = null,
    imageDescription: String? = null
) {
    ElevatedCard(
        onClick, modifier = modifier
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                title,
                Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp),
                fontSize = 25.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                subtitle,
                Modifier.fillMaxWidth().padding(top = 20.dp),
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Text(
                previewText,
                Modifier.padding(30.dp).padding(top = 40.dp).fillMaxWidth(),
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(50.dp))

            if (image != null) KamelImage(
                image, imageDescription, modifier = Modifier.fillMaxSize().padding(50.dp), alignment = Alignment.Center
            )
        }
    }
}