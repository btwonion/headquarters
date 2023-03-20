package dev.nyon.headquarters.gui.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import compose.icons.FeatherIcons
import compose.icons.feathericons.*
import dev.nyon.headquarters.app.*
import dev.nyon.headquarters.app.launcher.auth.*
import dev.nyon.headquarters.app.launcher.launch
import dev.nyon.headquarters.app.profile.Profile
import dev.nyon.headquarters.app.profile.init
import dev.nyon.headquarters.app.profile.realm
import dev.nyon.headquarters.app.util.fabricProfile
import dev.nyon.headquarters.app.util.generateID
import dev.nyon.headquarters.connector.modrinth.models.project.version.Loader
import dev.nyon.headquarters.connector.quilt.requests.getLoaderProfile
import dev.nyon.headquarters.connector.quilt.requests.getLoadersOfGameVersion
import dev.nyon.headquarters.gui.gui.screen.HomeScreen
import dev.nyon.headquarters.gui.gui.screen.SearchScreen
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.realm.kotlin.ext.query
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

fun initGui() {
    application {
        var screen by remember { mutableStateOf(Screen.Home) }
        var theme by remember { mutableStateOf(darkTheme) }
        val profiles = remember { mutableStateListOf<Profile>() }
        var profile: Profile? by remember { mutableStateOf(null) }
        val findPublisher = realm.query<Profile>().find()
        profiles.addAll(findPublisher.toList())
        LaunchedEffect(true) {
            findPublisher.asFlow().collect {
                val foundProfile = it.list.firstOrNull() ?: return@collect
                profiles.removeIf { find -> find.profileID == foundProfile.profileID }
                profiles.add(foundProfile)
                if (profile?.profileID == foundProfile.profileID) profile = foundProfile
            }
        }
        val mcAccounts = remember {
            mutableStateListOf<MinecraftAccountInfo>().also {
                it.addAll(readAccountsFile())
            }
        }

        LaunchedEffect(true) {
            if (mcAccounts.isNotEmpty()) mcAccounts.forEach {
                val mcProfile = ktorClient.get(Url(MinecraftAuth.minecraftProfileRequestUrl)) {
                    header("Authorization", "Bearer ${it.accessToken}")
                }.body<MinecraftProfile>()
                if (mcProfile.name != it.username) it.username = mcProfile.name
            }
            saveAccountsFile(mcAccounts)
        }

        var currentAccount by remember { mutableStateOf(mcAccounts.firstOrNull()) }

        if (profile == null) appScope.launch {
            profile =
                Profile().apply {
                    name = "Profile 1"
                    profileID = generateID()
                    loader = Loader.Quilt
                    minecraftVersion =
                        mojangConnector.getVersionPackage(mojangConnector.getVersionManifest()!!.latest.release)!!

                    val latestLoaderVersion = (quiltConnector.getLoadersOfGameVersion(
                        minecraftVersion.id
                    )?.first()
                        ?: error("Cannot find compatible fabric loader for version '${minecraftVersion.id}'")).loader.version
                    loaderVersion = latestLoaderVersion
                    loaderProfile = quiltConnector.getLoaderProfile(
                        latestLoaderVersion,
                        minecraftVersion.id
                    )?.fabricProfile()
                        ?: error("Cannot find compatible fabric loader for version '${minecraftVersion.id}'")
                    profileDir = runningDir.resolve("profiles/Profile-1/")
                }
            profile?.init()
            realm.write {
                copyToRealm(profile!!)
            }
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
                                { Text(profile?.name ?: "Loading...") },
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
                                opened, { opened = false }, modifier = Modifier.background(theme.primary).clip(
                                    RoundedCornerShape(8.dp)
                                )
                            ) {
                                mcAccounts.forEachIndexed { index, account ->
                                    DropdownMenuItem({
                                        currentAccount = account
                                    }, enabled = account != currentAccount) {
                                        Text(
                                            account.username,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxSize(),
                                            fontWeight = if (account == currentAccount) FontWeight.Bold else null
                                        )
                                    }
                                    if (index == mcAccounts.size - 1) Divider(Modifier.fillMaxWidth().padding(2.dp))
                                }

                                DropdownMenuItem({
                                    appScope.launch {
                                        MinecraftAuth {
                                            if (mcAccounts.find { it.uuid == uuid } != null)
                                                mcAccounts.removeAll { it.uuid == uuid }
                                            currentAccount = this@MinecraftAuth
                                            mcAccounts.add(this@MinecraftAuth)
                                            saveAccountsFile(mcAccounts)
                                        }.prepareLogIn()
                                    }
                                }, enabled = true) {
                                    Text(
                                        "Add account",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxSize()
                                    )
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
                        Modifier.fillMaxHeight().background(theme.surfaceVariant),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        IconButton(onClick = {
                            //screen = Screen.Launch
                            appScope.launch {
                                if ((currentAccount != null) && (currentAccount!!.expireDate > Clock.System.now())) {
                                    profile?.launch(currentAccount!!)
                                    return@launch
                                }
                                MinecraftAuth {
                                    profile?.launch(this@MinecraftAuth)
                                    mcAccounts.add(this@MinecraftAuth.also { currentAccount = it })
                                    saveAccountsFile(mcAccounts)
                                }.prepareLogIn()
                            }
                        }, Modifier.padding(10.dp)) {
                            Icon(FeatherIcons.Play, "launch")
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