package dev.nyon.headquarters.gui.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.gui.gui.screen.HomeScreen
import dev.nyon.headquarters.gui.gui.screen.search.SearchScreen

fun initGui() {
    application {
        var screen by remember { mutableStateOf(Screen.Home) }
        var theme by remember { mutableStateOf(darkTheme) }

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
                        IconButton({ screen = Screen.Home }, Modifier.padding(5.dp)) { Icon(FeatherIcons.Home, "home") }
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
                                { Text("current game profile") },
                                { opened = true },
                                Modifier.padding(5.dp),
                                icon = { Icon(FeatherIcons.Package, "game profile") },
                                contentColor = theme.primaryContainer,
                                backgroundColor = theme.onPrimaryContainer
                            )

                            DropdownMenu(
                                opened, { opened = false }, modifier = Modifier.background(theme.primary)
                            ) {
                                DropdownMenuItem({}) {
                                    Text("coclcl", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
                                }
                                Divider()
                                DropdownMenuItem({}) {
                                    Text("coclcl", textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
                                }
                            }
                        }

                        /**
                         * Account button with dropdownMenu
                         */
                        Box {
                            var opened by remember { mutableStateOf(false) }
                            IconButton({ opened = true }, Modifier.padding(5.dp)) {
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
                        IconButton({}, Modifier.padding(5.dp)) {
                            Icon(FeatherIcons.Compass, "discover")
                        }

                        IconButton({ screen = Screen.Search }, Modifier.padding(5.dp)) {
                            Icon(FeatherIcons.Search, "search")
                        }

                        IconButton({}, Modifier.padding(5.dp)) {
                            Icon(FeatherIcons.Archive, "mods")
                        }

                        Spacer(Modifier.size(20.dp))
                        IconButton({
                            theme = if (theme == darkTheme) lightTheme else darkTheme
                        }, Modifier.padding(5.dp)) {
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
                            Screen.Home -> this.HomeScreen()
                            Screen.Search -> SearchScreen()
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