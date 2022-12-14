package dev.nyon.headquarters.gui

import dev.nyon.headquarters.app.initApp
import dev.nyon.headquarters.gui.gui.initGui
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

lateinit var appScope: CoroutineScope
suspend fun main() = coroutineScope {
    appScope = this
    initApp()
    launch {
        initGui()
    }
    println("Successfully started")
}