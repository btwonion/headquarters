package dev.nyon.headquarter.node

import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.terminal.Terminal
import dev.nyon.headquarter.api.database.initMongoDbs
import dev.nyon.headquarter.node.commands.HeadquarterCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

lateinit var mainScope: CoroutineScope
val terminal = Terminal()

suspend fun main() {
    try {
        coroutineScope {
            mainScope = this
            initMongoDbs()
            loadCaches()

            launch {
                while (true) {
                    if (readlnOrNull() != null) HeadquarterCommand().main(readln().split(" "))
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        terminal.println(red("The manager shut down cause of an internal error!"))
        exitProcess(0)
    }
}