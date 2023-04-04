package dev.nyon.headquarters.gui.screens.search.popup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI

@Composable
fun ProjectSpec(icon: ImageVector, text: String, link: String, color: Color = Color.Unspecified) {
    val annotatedLinkString = buildAnnotatedLinkString(text, link, color = color)

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

fun buildAnnotatedLinkString(
    text: String,
    link: String,
    fontStyle: FontStyle? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = Color.Unspecified
) =
    buildAnnotatedString {
        append(text)

        addStyle(
            style = SpanStyle(
                color,
                textDecoration = TextDecoration.Underline,
                fontStyle = fontStyle,
                fontSize = fontSize
            ), start = 0, end = text.length
        )

        addStringAnnotation(tag = "URL", annotation = link, 0, text.length)
    }

@Composable
fun LinkText(annotatedString: AnnotatedString, modifier: Modifier = Modifier) {
    ClickableText(annotatedString, modifier) {
        annotatedString.getStringAnnotations("URL", it, it).firstOrNull()?.let { stringAnnotation ->
            Desktop.getDesktop().browse(URI(stringAnnotation.item))
        }
    }
}