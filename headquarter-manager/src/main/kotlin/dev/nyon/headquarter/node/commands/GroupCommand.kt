package dev.nyon.headquarter.node.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.TextColors.*
import dev.nyon.headquarter.api.database.groups
import dev.nyon.headquarter.api.group.Group
import dev.nyon.headquarter.api.group.Template
import dev.nyon.headquarter.node.groupCache
import dev.nyon.headquarter.node.templateCache
import dev.nyon.headquarter.node.terminal
import org.litote.kmongo.eq
import java.util.*

class GroupCommand : CliktCommand(name = "group", help = "This is the root headquarter group command") {

    init {
        subcommands(Create(), Info(), Remove())
    }

    override fun run() {}

    inner class Create : CliktCommand(name = "create", help = "Creates a new group") {

        private val name by option(help = "The name of the new group").prompt("Name")
        private val description by option(help = "The description for the new group").prompt("$name's description")
        private val template by option(help = "The template for the new group").choice(kotlin.run {
            val choices = mutableMapOf<String, Template>()
            templateCache.forEach {
                choices[it.value.name] = it.value
            }
            return@run choices
        }).prompt()
        private val maxMemory by option(help = "The maximum memory of each service").int()
            .prompt("Maximum memory of each service (in mb)").check { it % 2 == 0 }
        private val minRunningServices by option(help = "The minimum number of running services").int()
            .prompt("Minimum running services")
        private val maxRunningServices by option(help = "The maximum number of running services").int()
            .prompt("Maximum running services (uncapped = -1)")
        private val static by option("-static", "-s").flag()

        override fun run() = launchJob {
            var newUUID = UUID.randomUUID()
            while (groupCache.containsKey(newUUID)) newUUID = UUID.randomUUID()

            val group = Group(
                newUUID, name, description, template, static, maxMemory, maxRunningServices, minRunningServices
            )

            groupCache[newUUID] = group
            groups.insertOne(group)

            terminal.println("${green("The group $name was successfully created and has the uuid ")}${blue(newUUID.toString())}")
        }
    }

    inner class Info : CliktCommand(name = "info", help = "Displays info about the provided group") {

        private val group by argument(
            "group", "The group from which the information gets retrieved"
        ).choice(kotlin.run {
            val choices = mutableMapOf<String, Group>()
            groupCache.forEach {
                choices[it.value.name] = it.value
            }
            return@run choices
        })

        override fun run() = launchJob {
            mapOf(
                "Name" to group.name,
                "UUID" to group.uuid.toString(),
                "Description" to group.description,
                "Template" to "${group.template.name} - ${group.template.uuid.toString()}",
                "Static" to group.static.toString(),
                "Max memory" to group.maxMemory.toString(),
                "Max running services" to group.maxRunningServices.toString(),
                "Min running services" to group.minRunningServices.toString()
            ).forEach {
                terminal.println("${gray(it.key)}${blue(it.value)}")
            }
        }
    }

    inner class Remove : CliktCommand(name = "remove", help = "Deletes the given group") {

        private val group by argument(
            "group", "The group which should be deleted"
        ).choice(kotlin.run {
            val choices = mutableMapOf<String, Group>()
            groupCache.forEach {
                choices[it.value.name] = it.value
            }
            return@run choices
        })

        override fun run() = launchJob {
            groups.deleteOne(Group::uuid eq group.uuid)
            groupCache.remove(group.uuid)

            terminal.println("${green("The group ")}${blue(group.name)}${green(" was removed successfully!")}")
        }

    }

}