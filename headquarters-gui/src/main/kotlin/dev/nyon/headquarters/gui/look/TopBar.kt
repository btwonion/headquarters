package dev.nyon.headquarters.gui.look

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Package
import compose.icons.feathericons.User
import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.launcher.auth.MinecraftAccountInfo
import dev.nyon.headquarters.app.launcher.auth.MinecraftAuth
import dev.nyon.headquarters.app.launcher.auth.saveAccountsFile
import dev.nyon.headquarters.app.profile.Profile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

context (ColumnScope)
@Composable
fun TopBar(
    theme: ColorScheme,
    profile: Profile?,
    profiles: SnapshotStateList<Profile>,
    mcAccounts: SnapshotStateList<MinecraftAccountInfo>,
    mcAccount: MinecraftAccountInfo?,
    profileSwitchCallback: Profile.() -> Unit,
    mcAccountSwitchCallback: MinecraftAccountInfo.() -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().background(theme.primary), horizontalArrangement = Arrangement.End) {
        // Profile management
        ProfileBox(profile, profiles, profileSwitchCallback)

        // Minecraft account management
        AccountBox(theme, mcAccount, mcAccounts, mcAccountSwitchCallback)
    }
}

context(RowScope)
@Composable
private fun ProfileBox(
    profile: Profile?,
    profiles: SnapshotStateList<Profile>,
    profileSwitchCallback: Profile.() -> Unit
) {
    Box {
        var openedDropdownMenu by remember { mutableStateOf(false) }
        ExtendedFloatingActionButton(
            { Text(profile?.name ?: "Loading...") },
            { Icon(FeatherIcons.Package, "game profile") },
            { openedDropdownMenu = true },
            Modifier.padding(5.dp)
        )

        // Profile Picker
        DropdownMenu(
            openedDropdownMenu,
            { openedDropdownMenu = false },
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            profiles.forEachIndexed { index, localProfile ->
                DropdownMenuItem({
                    Text(
                        localProfile.name,
                        textAlign = TextAlign.Center,
                        fontWeight = if (localProfile == profile) FontWeight.Bold else null,
                        modifier = Modifier.fillMaxSize()
                    )
                }, {
                    profileSwitchCallback(localProfile)
                }, enabled = profile == localProfile)
                if (index < profiles.size - 1) Divider()
            }
        }
    }
}

context(RowScope)
@Composable
private fun AccountBox(
    theme: ColorScheme,
    mcAccount: MinecraftAccountInfo?,
    mcAccounts: SnapshotStateList<MinecraftAccountInfo>,
    mcAccountSwitchCallback: MinecraftAccountInfo.() -> Unit
) {
    Box {
        // User Button
        var openedDropdownMenu by remember { mutableStateOf(false) }
        IconButton({ openedDropdownMenu = true }, Modifier.padding(10.dp)) {
            Icon(FeatherIcons.User, "account", tint = theme.onPrimary)
        }

        // Account Picker
        DropdownMenu(
            openedDropdownMenu,
            { openedDropdownMenu = false },
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            // Minecraft accounts
            mcAccounts.forEachIndexed { index, account ->
                DropdownMenuItem({
                    Text(
                        account.username,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize(),
                        fontWeight = if (account == mcAccount) FontWeight.Bold else null
                    )
                }, {
                    mcAccountSwitchCallback(account)
                }, enabled = account != mcAccount)
                if (index == mcAccounts.size - 1) Divider(Modifier.fillMaxWidth().padding(2.dp))
            }

            // Add new account button
            var enabled by remember { mutableStateOf(true) }
            DropdownMenuItem({
                Text(
                    "Add account",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }, {
                appScope.launch {
                    enabled = false
                    launch {
                        delay(1.minutes)
                        enabled = true
                    }
                    MinecraftAuth {
                        if (mcAccounts.find { it.uuid == uuid } != null)
                            mcAccounts.removeAll { it.uuid == uuid }
                        mcAccountSwitchCallback(this@MinecraftAuth)
                        mcAccounts.add(this@MinecraftAuth)
                        saveAccountsFile(mcAccounts)
                        enabled = true
                    }.prepareLogIn()
                }
            }, enabled = enabled)
        }
    }
}