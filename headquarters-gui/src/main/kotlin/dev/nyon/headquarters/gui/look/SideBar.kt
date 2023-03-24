package dev.nyon.headquarters.gui.look

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.user.updateUserSetting
import dev.nyon.headquarters.gui.Screen
import kotlinx.coroutines.launch

context(RowScope)
@Composable
fun SideBar(
    theme: ColorScheme,
    screenSwitchCallback: Screen.() -> Unit
) {
    Column(
        Modifier.fillMaxHeight().background(theme.primary),
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
        }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.Play, "launch", tint = theme.onPrimary)
        }

        // Search screen button
        IconButton({ screenSwitchCallback(Screen.Search) }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.Search, "search", tint = theme.onPrimary)
        }

        // Profiles screen button
        IconButton({ screenSwitchCallback(Screen.Profiles) }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.Archive, "profiles", tint = theme.onPrimary)
        }

        // Color switcher
        Spacer(Modifier.size(20.dp))
        IconButton({
            appScope.launch {
                updateUserSetting {
                    it.whiteTheme = theme != lightColorScheme()
                }
            }
        }, Modifier.padding(10.dp)) {
            Icon(
                if (theme == darkColorScheme()) FeatherIcons.Sun else FeatherIcons.Moon,
                "toggle theme",
                tint = theme.onPrimary
            )
        }
    }
}