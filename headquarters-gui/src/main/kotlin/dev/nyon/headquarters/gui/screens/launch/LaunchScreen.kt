package dev.nyon.headquarters.gui.screens.launch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.database.updateProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

context(BoxScope)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchScreen(theme: ColorScheme, profile: Profile?) {
    Column(Modifier.fillMaxSize()) {
        Text(
            "${profile?.name ?: "Loading..."} - ${profile?.minecraftVersionID}",
            Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = theme.onBackground
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            var jvmArgsInput by remember { mutableStateOf("") }
            var gameArgsInput by remember { mutableStateOf("") }
            LaunchedEffect(true) {
                delay(1.seconds)
                jvmArgsInput = profile?.extraJvmArgs?.joinToString(" ") ?: ""
                gameArgsInput = profile?.extraGameStartArgs?.joinToString(" ") ?: ""
            }
            TextField(
                jvmArgsInput,
                {
                    jvmArgsInput = it
                    if (profile != null) updateProfile(profile.profileID) {
                        this.extraJvmArgs = jvmArgsInput.split(" ").toMutableList()
                    }
                },
                singleLine = true,
                modifier = Modifier.padding(10.dp).defaultMinSize(500.dp),
                supportingText = { Text("optional jvm launch arguments") }
            )

            TextField(
                gameArgsInput,
                {
                    gameArgsInput = it
                    if (profile != null) appScope.launch {
                        updateProfile(profile.profileID) {
                            this.extraGameStartArgs = gameArgsInput.split(" ").toMutableList()
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.padding(10.dp).defaultMinSize(500.dp),
                supportingText = { Text("optional game launch arguments") }
            )
        }
        var maxMemoryInput by remember { mutableStateOf(profile?.memory ?: 4) }

        TextField(
            maxMemoryInput.toString(),
            {
                maxMemoryInput = it.toIntOrNull() ?: 4
                if (profile != null) appScope.launch {
                    updateProfile(profile.profileID) {
                        this.memory = maxMemoryInput
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.padding(10.dp).defaultMinSize(200.dp).align(Alignment.CenterHorizontally),
            supportingText = { Text("maximum memory usage in gb") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}