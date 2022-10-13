package dev.nyon.headquarters.gui.gui.screen

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
import io.kamel.image.lazyPainterResource
import io.ktor.http.*

context(BoxScope)
@Composable
fun HomeScreen() {
    NewsCard(
        {},
        Modifier.align(Alignment.TopStart).padding(50.dp).size(500.dp, 800.dp),
        "Latest Minecraft News",
        "Subtitle oder so",
        "trtrtrotkrotkorkoasdjdoiashjdoashduioashdiuashgdhjasgdjhkasgdjhkgasdjhkgasjhdkgasjhkdgasjhkgdjhkasgdjhkasghjdgasjhdgasjhdgasjhdgajshdgjahksgdjhasgdjhasd",
        lazyPainterResource(Url("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.redd.it%2Flhyfpzbifpo21.png&f=1&nofb=1&ipt=a976be89f9fd9cd46c9f2c92fb49de59c7744d8d4cebc54f57f6fcb645aff793&ipo=images")),
        "minecraft"
    )

    NewsCard(
        {},
        Modifier.align(Alignment.TopCenter).padding(50.dp).padding(top = 150.dp).size(500.dp, 800.dp),
        "Changelog",
        "Mhh noch nichts",
        "einfach soooo oein krasser changelog nund es itst halt erst am anfang deswegen schweißen wir da einfach drauf. okay"
    )

    NewsCard(
        {},
        Modifier.align(Alignment.TopEnd).padding(50.dp).padding(top = 100.dp).size(500.dp, 800.dp),
        "Latest Modrinth Updates",
        "Subtitle oder so",
        "trtrtrotkrotkorkoasdjdoiashjdoashduioashdiuashgdhjasgdjhkasgdjhkgasdjhkgasjhdkgasjhkdgasjhkgdjhkasgdjhkasghjdgasjhdgasjhdgasjhdgajshdgjahksgdjhasgdjhasd",
        lazyPainterResource(Url("https://github.com/modrinth/art/blob/main/Branding/Mark/mark-dark__256x256.png?raw=true")),
        "modrinth"
    )
}


context(BoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsCard(
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