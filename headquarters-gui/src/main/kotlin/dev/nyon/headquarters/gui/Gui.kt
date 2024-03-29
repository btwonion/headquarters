package dev.nyon.headquarters.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.app.database.models.UserSettings
import dev.nyon.headquarters.app.database.profilesTrigger
import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.launcher.auth.*
import dev.nyon.headquarters.gui.look.SideBar
import dev.nyon.headquarters.gui.look.TopBar
import dev.nyon.headquarters.gui.screens.launch.LaunchScreen
import dev.nyon.headquarters.gui.screens.search.SearchScreen
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import dev.nyon.headquarters.app.database.profiles as dbProfiles

@OptIn(ExperimentalComposeUiApi::class)
fun initGui() {
    application {
        var userSettings by remember { mutableStateOf(UserSettings()) }
        var theme by remember { mutableStateOf(if (userSettings.whiteTheme) lightColorScheme() else darkColorScheme()) }
        // Listens for UserSetting changes
        LaunchedEffect(true) {
            dev.nyon.headquarters.app.database.userSettings.collect {
                userSettings = it
                theme = if (it.whiteTheme) lightColorScheme() else darkColorScheme()
            }
        }
        MaterialTheme(colorScheme = theme) {
            var screen by remember { mutableStateOf(Screen.Launch) }
            var profiles = remember { mutableStateListOf<Profile>() }
            var profile: Profile? by remember { mutableStateOf(null) }
            // Listens for profile changes
            LaunchedEffect(true) {
                profilesTrigger.collect { _ ->
                    profiles = mutableStateListOf(*dbProfiles.toTypedArray())
                    profile = dbProfiles.firstOrNull { it.selected } ?: dbProfiles.firstOrNull()
                }
            }

            val mcAccounts = remember {
                mutableStateListOf<MinecraftAccountInfo>().also {
                    it.addAll(readAccountsFile())
                }
            }
            var mcAccount by remember { mutableStateOf(mcAccounts.firstOrNull()) }
            // Checks account validity and name change
            LaunchedEffect(true) {
                if (mcAccounts.isNotEmpty()) mcAccounts.forEach {
                    val profileResponse = ktorClient.get(Url(MinecraftAuth.minecraftProfileRequestUrl)) {
                        header("Authorization", "Bearer ${it.accessToken}")
                    }
                    if (profileResponse.status == HttpStatusCode.Unauthorized) {
                        mcAccounts.remove(it)
                        return@forEach
                    }
                    val mcProfile = profileResponse.body<MinecraftProfile>()
                    if (mcProfile.name != it.username) it.username = mcProfile.name
                }
                saveAccountsFile(mcAccounts)
            }
            val exits = remember { mutableStateListOf<() -> Unit>() }

            Window(
                onCloseRequest = { this.exitApplication() },
                title = "Headquarters",
                onKeyEvent = {
                    if (it.key == Key.Escape && it.type == KeyEventType.KeyDown) {
                        (exits.lastOrNull() ?: return@Window true)()
                        exits.removeLastOrNull()
                    }
                    true
                },
                state = rememberWindowState(WindowPlacement.Maximized)
            ) {
                // Splits the window in two spaces
                Column {
                    // Creates the top bar
                    TopBar(
                        theme,
                        profile,
                        profiles,
                        mcAccounts,
                        mcAccount,
                        { profile = this },
                        { mcAccount = this }
                    )

                    // Creates the lower part of window
                    Row {
                        // Creates the sidebar
                        SideBar(
                            theme, screen
                        ) { screen = this }

                        // Creates the main surface
                        Box(Modifier.fillMaxSize().background(theme.surface)) {
                            when (screen) {
                                Screen.Search -> SearchScreen(theme, profile!!, exits)

                                Screen.Launch -> LaunchScreen(theme, profile)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}