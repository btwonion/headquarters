package dev.nyon.headquarters.gui.look

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.database.updateUserSettings
import dev.nyon.headquarters.app.database.userSettings
import dev.nyon.headquarters.gui.Screen

context(RowScope)
@Composable
fun SideBar(
    theme: ColorScheme,
    screen: Screen,
    screenSwitchCallback: Screen.() -> Unit
) {
    Column(
        Modifier.fillMaxHeight().background(theme.surfaceVariant),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Launch screen button
        IconButton(onClick = {
            screenSwitchCallback(Screen.Launch)
            // ("move code to launch screen")
            /* appScope.launch {
                if ((mcAccount != null) && (mcAccount.expireDate > Clock.System.now())) {
                    profile?.launch(mcAccount)
                    return@launch
                }
                MinecraftAuth {
                    profile?.launch(this@MinecraftAuth)
                    mcAccounts.add(this@MinecraftAuth.also { mcAccountSwitchCallback(it) })
                    saveAccountsFile(mcAccounts)
                }.prepareLogIn()
            }
             */
        }, Modifier.padding(10.dp), enabled = screen != Screen.Launch) {
            Icon(FeatherIcons.Play, "launch", tint = theme.onSurfaceVariant)
        }

        // Search screen button
        IconButton(
            { screenSwitchCallback(Screen.Search) },
            Modifier.padding(10.dp),
            enabled = screen != Screen.Search
        ) {
            Icon(FeatherIcons.Search, "search", tint = theme.onSurfaceVariant)
        }

        // Profiles screen button
        IconButton(
            { screenSwitchCallback(Screen.Profiles) },
            Modifier.padding(10.dp),
            enabled = screen != Screen.Profiles
        ) {
            Icon(FeatherIcons.Folder, "profiles", tint = theme.onSurfaceVariant)
        }

        // Color switcher
        Spacer(Modifier.size(20.dp))
        IconButton({ updateUserSettings { this.whiteTheme = !this.whiteTheme } }, Modifier.padding(10.dp)) {
            Icon(
                if (userSettings.value.whiteTheme) FeatherIcons.Moon else FeatherIcons.Sun,
                "toggle theme",
                tint = theme.onSurfaceVariant
            )
        }
    }
}