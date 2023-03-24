package dev.nyon.headquarters.gui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nyon.headquarters.app.ktorClient
import dev.nyon.headquarters.app.launcher.auth.*
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.createDefaultProfile
import dev.nyon.headquarters.app.profile.profileDB
import dev.nyon.headquarters.app.user.UserSettings
import dev.nyon.headquarters.app.user.createDefaultUserSettings
import dev.nyon.headquarters.app.user.userSettingDB
import dev.nyon.headquarters.gui.look.SideBar
import dev.nyon.headquarters.gui.look.TopBar
import dev.nyon.headquarters.gui.screens.launch.LaunchScreen
import dev.nyon.headquarters.gui.screens.search.SearchScreen
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.launch

fun initGui() {
    application {
        var userSettings by remember {
            mutableStateOf(
                userSettingDB.query(UserSettings::class).first().find() ?: createDefaultUserSettings()
            )
        }
        var theme by remember {
            mutableStateOf(if (userSettings.whiteTheme) lightColorScheme() else darkColorScheme())
        }
        LaunchedEffect(true) {
            val findPublisher = userSettingDB.query<UserSettings>().find()
            findPublisher.asFlow().collect { change ->
                when (change) {
                    is UpdatedResults -> {
                        userSettings = userSettingDB.query(UserSettings::class).first().find()!!
                        theme = if (userSettings.whiteTheme) lightColorScheme() else darkColorScheme()
                    }

                    else -> {}
                }
            }
        }
        MaterialTheme(colorScheme = theme) {
            var screen by remember { mutableStateOf(Screen.Launch) }
            val profiles = remember { mutableStateListOf<Profile>() }
            var profile: Profile? by remember { mutableStateOf(null) }
            // Listens for profile changes and loads them initially
            LaunchedEffect(true) {
                val findPublisher = profileDB.query<Profile>().find()
                profiles.addAll(findPublisher.toList())
                launch {
                    findPublisher.asFlow().collect { change ->
                        when (change) {
                            is UpdatedResults -> {
                                val foundProfiles = profileDB.query(Profile::class).find().toList()
                                profiles.clear()
                                profiles.addAll(foundProfiles)
                                profile =
                                    foundProfiles.find { it.profileID == profile?.profileID } ?: foundProfiles.first()
                            }

                            else -> {}
                        }
                    }
                }
                if (profiles.isNotEmpty()) profile = profiles.first()
                else createDefaultProfile()
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


            Window(
                onCloseRequest = { this.exitApplication() },
                title = "Headquarters",
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
                            theme
                        ) { screen = this }

                        // Creates the main surface
                        Box(Modifier.fillMaxSize()) {
                            when (screen) {
                                Screen.Search -> SearchScreen(theme, profile!!)

                                Screen.Launch -> LaunchScreen(profile)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}