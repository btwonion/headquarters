package dev.nyon.headquarters.gui.screens.home

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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


