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
import dev.nyon.headquarter.node.groupCache
import dev.nyon.headquarter.node.templateCache
import dev.nyon.headquarter.node.terminal
import org.litote.kmongo.eq
import java.util.*

class GroupCommand : CliktCommand(name = "group", help = "This is the root headquarter group command") {

    init {
        subcommands(Create(), Info(), Remove(), Modify(), List())
    }

    override fun run() {}

    inner class List : CliktCommand(name = "list", help = "Lists all groups") {
        override fun run() = launchJob {
            groupCache.forEach {
                terminal.println("${blue(it.value.name)} - ${white(it.value.uuid.toString())}")
            }
        }
    }

    inner class Modify : CliktCommand(name = "modify", help = "Modifies the given group") {
        private val group by groupArgument("The requested group")

        private val name by option("--name", "-n", help = "Sets the name of the group")
        private val description by option("--description", "-d", help = "Sets the description of the group")
        private val template by templateOption("Sets the template of the group")
        private val maxMemory by option(
            "--maxMemory", "--memory", "-m", help = "Sets the max memory for each service of the group"
        ).int()
        private val minRunningServices by option(
            "--minRunningServices", "--minServices", "-minS", help = "Sets the minimum of running services of the group"
        ).int()
        private val maxRunningServices by option(
            "--maxRunningServices", "--maxServices", "-maxS", help = "Sets the maximum of running services of the group"
        ).int()
        private val static by option(
            "--static", "-s", help = "Sets whether the services of the group are static or not"
        ).choice("true" to true, "false" to false)

        override fun run() = launchJob {
            if (name != null) group.name = name!!
            if (description != null) group.description = description!!
            if (template != null) group.template = template
            if (maxMemory != null) group.maxMemory = maxMemory!!
            if (minRunningServices != null) group.minRunningServices = minRunningServices!!
            if (maxRunningServices != null) group.maxRunningServices = maxRunningServices!!
            if (static != null) group.static = static!!
            groupCache[group.uuid] = group
            groups.replaceOne(Group::uuid eq group.uuid, group)
        }

    }

    inner class Create : CliktCommand(name = "create", help = "Creates a new group") {

        private val name by option(help = "The name of the new group").prompt("Name")
        private val description by option(help = "The description for the new group").prompt("$name's description")
        private val template by templateOption("The template of the new group").prompt("Template")
        private val maxMemory by option(help = "The maximum memory of each service").int()
            .prompt("Maximum memory of each service (in mb)").check { it % 2 == 0 }
        private val minRunningServices by option(help = "The minimum number of running services").int()
            .prompt("Minimum running services")
        private val maxRunningServices by option(help = "The maximum number of running services").int()
            .prompt("Maximum running services (uncapped = -1)")
        private val static by option("--static", "-s").flag()

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

        private val group by groupArgument("The group from which the information gets retrieved")

        override fun run() = launchJob {
            mapOf(
                "Name" to group.name,
                "UUID" to group.uuid.toString(),
                "Description" to group.description,
                "Template" to "${group.template?.name} - ${group.template?.uuid.toString()}",
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

        private val group by groupArgument("The group which should be deleted")

        override fun run() = launchJob {
            groups.deleteOne(Group::uuid eq group.uuid)
            groupCache.remove(group.uuid)

            terminal.println("${green("The group ")}${blue(group.name)}${green(" was removed successfully!")}")
        }

    }
}

fun CliktCommand.groupArgument(description: String) = argument(
    "group", description
).choice(kotlin.run {
    val choices = mutableMapOf<String, Group>()
    groupCache.forEach {
        choices[it.value.name] = it.value
    }
    return@run choices
})

fun CliktCommand.templateOption(description: String) =
    option("--template", "-t", help = description).choice(kotlin.run {
        val choices = mutableMapOf<String, dev.nyon.headquarter.api.group.Template>()
        templateCache.forEach {
            choices[it.value.name] = it.value
        }
        return@run choices
    })