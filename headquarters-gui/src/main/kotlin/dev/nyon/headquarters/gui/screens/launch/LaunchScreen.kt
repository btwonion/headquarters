package dev.nyon.headquarters.gui.screens.launch

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.nyon.headquarters.app.profile.Profile

context(BoxScope)
@Composable
fun LaunchScreen(profile: Profile?) {
    Column(Modifier.fillMaxSize()) {
        Text(
            "${profile?.name ?: "Loading..."} - ${profile?.minecraftVersionID}",
            Modifier.align(Alignment.CenterHorizontally),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
    }
}