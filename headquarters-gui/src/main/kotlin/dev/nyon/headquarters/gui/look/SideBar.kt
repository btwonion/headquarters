package dev.nyon.headquarters.gui.look

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.launcher.auth.MinecraftAccountInfo
import dev.nyon.headquarters.app.launcher.auth.MinecraftAuth
import dev.nyon.headquarters.app.launcher.auth.saveAccountsFile
import dev.nyon.headquarters.app.launcher.launch
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.gui.Screen
import dev.nyon.headquarters.gui.darkTheme
import dev.nyon.headquarters.gui.lightTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

context(RowScope)
@Composable
fun SideBar(
    profile: Profile?,
    mcAccount: MinecraftAccountInfo?,
    mcAccounts: SnapshotStateList<MinecraftAccountInfo>,
    theme: ColorScheme,
    mcAccountSwitchCallback: MinecraftAccountInfo.() -> Unit,
    screenSwitchCallback: Screen.() -> Unit,
    themeSwitchCallback: ColorScheme.() -> Unit
) {
    Column(
        Modifier.fillMaxHeight().background(theme.surfaceVariant),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Launch screen button
        IconButton(onClick = {
            //screen = Screen.Launch
            appScope.launch {
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
        }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.Play, "launch")
        }

        // Search screen button
        IconButton({ screenSwitchCallback(Screen.Search) }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.Search, "search")
        }

        // Profiles screen button
        IconButton({ screenSwitchCallback(Screen.Profiles) }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.Archive, "profiles")
        }

        // Color switcher
        Spacer(Modifier.size(20.dp))
        IconButton({
            themeSwitchCallback(if (theme == darkTheme) lightTheme else darkTheme)
        }, Modifier.padding(10.dp)) {
            Icon(
                if (theme == darkTheme) FeatherIcons.Sun else FeatherIcons.Moon, "toggle theme"
            )
        }
    }
}