package dev.nyon.headquarter.manager.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.nyon.headquarter.manager.mainScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HeadquarterCommand : CliktCommand(name = "headquarter", help = "This is the headquarter root command") {

    init {
        subcommands(GroupCommand(), TemplateCommand())
    }

    override fun run() {}
}


fun launchJob(
    block: suspend CoroutineScope.() -> Unit
) {
    mainScope.launch(block = block)
}