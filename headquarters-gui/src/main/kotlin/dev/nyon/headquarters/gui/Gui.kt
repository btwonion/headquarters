package dev.nyon.headquarters.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import dev.nyon.headquarters.app.profile.createNewProfile
import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.gui.look.SideBar
import dev.nyon.headquarters.gui.look.TopBar
import dev.nyon.headquarters.gui.screens.home.HomeScreen
import dev.nyon.headquarters.gui.screens.search.SearchScreen
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.UpdatedResults

fun initGui() {
    application {
        var screen by remember { mutableStateOf(Screen.Home) }
        var theme by remember { mutableStateOf(darkTheme) }
        val profiles = remember { mutableStateListOf<Profile>() }
        var profile: Profile? by remember { mutableStateOf(null) }
        // Listesn for profile changes and loads them initially
        LaunchedEffect(true) {
            val findPublisher = realm.query<Profile>().find()
            profiles.addAll(findPublisher.toList())
            if (profiles.isNotEmpty()) profile = profiles.first()
            else createNewProfile()
            findPublisher.asFlow().collect { change ->
                when (change) {
                    is UpdatedResults -> {
                        val foundProfiles = realm.query(Profile::class).find().toList()
                        profiles.clear()
                        profiles.addAll(foundProfiles)
                        profile = foundProfiles.find { it.profileID == profile?.profileID } ?: foundProfiles.first()
                    }

                    else -> {}
                }
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


        Window(
            onCloseRequest = { this.exitApplication() },
            title = "Headquarters",
            state = rememberWindowState(WindowPlacement.Maximized)
        ) {
            // Splits the window in two spaces
            Column {
                // Creates the top bar
                TopBar(
                    profile,
                    profiles,
                    mcAccounts,
                    mcAccount,
                    theme,
                    { screen = this },
                    { profile = this },
                    { mcAccount = this }
                )

                // Creates the lower part of window
                Row {
                    // Creates the sidebar
                    SideBar(
                        profile,
                        mcAccount,
                        mcAccounts,
                        theme,
                        { mcAccount = this },
                        { screen = this },
                        { theme = this }
                    )

                    // Creates the main surface
                    Box(Modifier.fillMaxSize().background(theme.background)) {
                        when (screen) {
                            Screen.Home -> HomeScreen()
                            Screen.Search -> SearchScreen(theme, profile!!)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

val darkTheme = darkColorScheme()
val lightTheme = lightColorScheme()