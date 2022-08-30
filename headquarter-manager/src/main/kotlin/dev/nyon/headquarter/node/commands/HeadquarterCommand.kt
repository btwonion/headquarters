package dev.nyon.headquarter.node.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.nyon.headquarter.node.mainScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HeadquarterCommand : CliktCommand(name = "headquarter", help = "This is the headquarter root command") {

    init {
        subcommands(GroupCommand())
    }

    override fun run() {}
}


fun launchJob(
    block: suspend CoroutineScope.() -> Unit
) {
    mainScope.launch(block = block)
}