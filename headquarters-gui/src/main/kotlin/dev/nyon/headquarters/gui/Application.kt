package dev.nyon.headquarters.gui

import dev.nyon.headquarters.app.initApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

lateinit var guiScope: CoroutineScope
suspend fun main() = coroutineScope {
    guiScope = this
    initApp()
    launch {
        initGui()
    }
    println("Successfully started")
}