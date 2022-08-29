package dev.nyon.headquarter.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.system.exitProcess

lateinit var mainScope: CoroutineScope
suspend fun main() {
    try {
        coroutineScope {
            mainScope = this
            configureWebsockets()
            configureRealmConfiguration()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(0)
    }
}