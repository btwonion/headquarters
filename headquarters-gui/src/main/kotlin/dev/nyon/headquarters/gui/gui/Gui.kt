package dev.nyon.headquarters.gui.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import compose.icons.FeatherIcons
import compose.icons.feathericons.*

fun initGui() {
    application {
        var theme by remember { mutableStateOf(darkColors) }

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
                    Box(modifier = Modifier.background(theme.onBackground)) {
                        IconButton({}, Modifier.padding(5.dp)) { Icon(FeatherIcons.Home, "home") }
                    }

                    /**
                     * The top bar entries
                     */
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth().background(theme.onBackground)
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
                                contentColor = theme.primary,
                                backgroundColor = theme.primaryVariant
                            )

                            DropdownMenu(
                                opened, { opened = false }, modifier = Modifier.background(theme.secondaryVariant)
                            ) {
                                DropdownMenuItem({}) {
                                    Text("coclcl")
                                }
                                Divider()
                                DropdownMenuItem({}) {
                                    Text("asdaw")
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
                                opened, { opened = false }, modifier = Modifier.background(theme.secondaryVariant)
                            ) {
                                DropdownMenuItem({}) {
                                    Text("coclcl")
                                }
                                Divider()
                                DropdownMenuItem({}) {
                                    Text("asdaw")
                                }
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
                        Modifier.fillMaxHeight().background(theme.onSurface), verticalArrangement = Arrangement.Bottom
                    ) {
                        // sidebar
                        IconButton({ theme = if (theme == darkColors) lightColors else darkColors }) {
                            Icon(if (theme == darkColors) FeatherIcons.Sun else FeatherIcons.Moon, "toggle theme")
                        }
                    }

                    /**
                     * The main surface
                     */
                    Box(Modifier.fillMaxSize().background(theme.background)) {

                    }
                }
            }
        }
    }
}


/**
 * Color usage:
 * primary - writing
 * primaryVariant - writing background
 * secondary - second primary writing
 * secondaryVariant - second primary background
 * background - background
 * surface - surface
 * error - error
 * onPrimary - primary hover
 * onSecondary - second hover
 * onBackground - second background color
 * onSurface - third background color
 * onError - error hover
 */
val darkColors = Colors(
    Color(0x991B3A4B),
    Color(0x99287379),
    Color(0x99144552),
    Color(0x990B525B),
    Color(0x993E1F47),
    Color(0x994D194D),
    Color(0x99391017),
    Color(0x99312244),
    Color(0x99272640),
    Color(0x99065A60),
    Color(0x99006466),
    Color(0x997A493B),
    false
)

val lightColors = Colors(
    Color(0x992A6F97),
    Color(0x992C7DA0),
    Color(0x99468FAF),
    Color(0x9961A5C2),
    Color(0x99014F86),
    Color(0x9901497C),
    Color(0x9989C2D9),
    Color(0x99312244),
    Color(0x99A9D6E5),
    Color(0x99012A4A),
    Color(0x99013A63),
    Color(0x997A493B),
    true
)