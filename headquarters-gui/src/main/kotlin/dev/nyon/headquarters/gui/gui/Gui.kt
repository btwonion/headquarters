package dev.nyon.headquarters.gui.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.appScope
import dev.nyon.headquarters.app.launcher.auth.MinecraftAuth
import dev.nyon.headquarters.app.launcher.launch
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.init
import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.app.profile.testMinecraftVersion
import dev.nyon.headquarters.app.runningDir
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.gui.gui.screen.HomeScreen
import dev.nyon.headquarters.gui.gui.screen.SearchScreen
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch

fun initGui() {
    application {
        var screen by remember { mutableStateOf(Screen.Home) }
        var theme by remember { mutableStateOf(darkTheme) }
        var profile by remember {
            mutableStateOf(realm.query<Profile>().find().getOrNull(0) ?: kotlin.run {
                val newProfile =
                    Profile().apply {
                        name = "Profile 1"
                        profileID = "sadawdwad"
                        loader = Loader.Fabric
                        profileDir = runningDir.resolve("profiles/${"SDAWDSAD"}/")
                        minecraftVersion = testMinecraftVersion
                    }
                newProfile.init()
                newProfile
            })
        }

        Window(
            onCloseRequest = { this.exitApplication() },
            title = "Headquarters",
            state = rememberWindowState(WindowPlacement.Maximized)
        ) {
            /**
             * Splits the window into two horizontal spaces
             */
            Column {
                /**
                 * Creates the layout for the top bar
                 */
                Row(modifier = Modifier.fillMaxWidth()) {
                    /**
                     * Home Button
                     */
                    Box(modifier = Modifier.background(theme.surfaceVariant)) {
                        IconButton({ screen = Screen.Home }, Modifier.padding(10.dp)) {
                            Icon(
                                FeatherIcons.Home,
                                "home"
                            )
                        }
                    }

                    /**
                     * The top bar entries
                     */
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth().background(theme.surfaceVariant)
                    ) {
                        /**
                         * Game profile button with dropdownMenu
                         */
                        Box {
                            var opened by remember { mutableStateOf(false) }
                            ExtendedFloatingActionButton(
                                { Text(profile.name) },
                                { Icon(FeatherIcons.Package, "game profile") },
                                { opened = true },
                                Modifier.padding(5.dp),
                                contentColor = theme.primaryContainer
                            )

                            DropdownMenu(
                                opened, { opened = false }, modifier = Modifier.background(theme.primary).clip(
                                    RoundedCornerShape(8.dp)
                                )
                            ) {
                                val profiles = realm.query<Profile>().find()
                                profiles.forEachIndexed { index, localProfile ->
                                    DropdownMenuItem({
                                        profile = localProfile
                                    }, enabled = profile == localProfile) {
                                        Text(
                                            localProfile.name,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    if (index < profiles.size - 1) Divider()
                                }
                            }
                        }

                        /**
                         * Account button with dropdownMenu
                         */
                        Box {
                            var opened by remember { mutableStateOf(false) }
                            IconButton({ opened = true }, Modifier.padding(10.dp)) {
                                Icon(FeatherIcons.User, "account")
                            }

                            DropdownMenu(
                                opened, { opened = false }, modifier = Modifier.background(theme.primary)
                            ) {

                            }
                        }
                    }
                }

                /**
                 * Second part of the window
                 */
                Row {
                    /**
                     * Creates the sidebar
                     */
                    Column(
                        Modifier.fillMaxHeight().background(theme.surfaceVariant),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        IconButton(onClick = {
                            //screen = Screen.Launch
                            appScope.launch {
                                MinecraftAuth { minecraftCredentials, xSTSCredentials ->
                                    profile.launch(minecraftCredentials, xSTSCredentials)
                                }.prepareLogIn()
                            }
                        }, Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Play, "launch")
                        }

                        IconButton({ screen = Screen.Discover }, Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Compass, "discover")
                        }

                        IconButton({ screen = Screen.Search }, Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Search, "search")
                        }

                        IconButton({ screen = Screen.Mods }, Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Archive, "mods")
                        }

                        Spacer(Modifier.size(20.dp))
                        IconButton({
                            theme = if (theme == darkTheme) lightTheme else darkTheme
                        }, Modifier.padding(10.dp)) {
                            Icon(
                                if (theme == darkTheme) FeatherIcons.Sun else FeatherIcons.Moon, "toggle theme"
                            )
                        }
                    }

                    /**
                     * The main surface
                     */
                    Box(Modifier.fillMaxSize().background(theme.background)) {
                        when (screen) {
                            Screen.Home -> HomeScreen()
                            Screen.Search -> SearchScreen(theme)
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