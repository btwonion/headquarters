package dev.nyon.headquarter.manager.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.TextColors.*
import dev.nyon.headquarter.api.database.templates
import dev.nyon.headquarter.api.group.Template
import dev.nyon.headquarter.api.networking.Host
import dev.nyon.headquarter.manager.templateCache
import dev.nyon.headquarter.manager.terminal
import org.litote.kmongo.eq
import java.util.*

class TemplateCommand : CliktCommand(name = "template", help = "The root template command") {

    init {
        subcommands(Create(), Remove(), Modify(), Info())
    }

    override fun run() {}

    inner class Info : CliktCommand(name = "info", help = "Displays info about a template") {

        private val template by templateArgument("The template of which the info should be retrieved")

        override fun run() = launchJob {
            mapOf(
                "Name" to template.name,
                "UUID" to template.uuid.toString(),
                "Hostname" to template.host.ip,
                "Path" to template.path,
                "Extra Ports" to template.extraPorts.joinToString()
            ).forEach {
                terminal.println("${gray(it.key)}${blue(it.value)}")
            }
        }

    }

    inner class Modify : CliktCommand(name = "modify", help = "Modify a template") {

        private val template by templateArgument("The template to modify")

        private val name by option("--name", "-n", help = "Sets the name of the template")
        private val path by option("--path", "-p", help = "Sets the path of the template")
        private val hostName by option("--host", "--hostname", "-h", help = "Sets the hostname of the template source")
        private val extraPorts by option("--ports", "--extraPorts", help = "Sets the extra ports of the template").int()
            .multiple()

        override fun run() = launchJob {
            if (name != null) template.name = name!!
            if (path != null) template.path = path!!
            if (hostName != null) template.host = Host(hostName!!, null)
            template.extraPorts = extraPorts
            templateCache[template.uuid] = template
            templates.replaceOne(Template::uuid eq template.uuid, template)
        }

    }

    inner class List : CliktCommand(name = "list", help = "Lists all templates") {
        override fun run() = launchJob {
            templateCache.forEach {
                terminal.println("${blue(it.value.name)} - ${white(it.value.uuid.toString())}")
            }
        }
    }

    inner class Create : CliktCommand(name = "create", help = "Creates a new template") {

        private val name by option("name").prompt("Name")
        private val hostName by option("host").prompt("Hostname")
        private val directory by option("directory").prompt("Path")

        override fun run() = launchJob {
            var newUUID = UUID.randomUUID()
            while (templateCache.containsKey(newUUID)) newUUID = UUID.randomUUID()

            val template = Template(newUUID, name, Host(hostName, null), directory, listOf())
            templateCache[template.uuid] = template
            templates.replaceOne(Template::uuid eq template.uuid, template)
            terminal.println(green("The template was created!"))
        }

    }

    inner class Remove : CliktCommand(name = "remove", help = "Removes a template") {

        private val template by templateArgument("The template which should be removed")

        override fun run() = launchJob {
            templateCache.remove(template.uuid)
            templates.deleteOne(Template::uuid eq template.uuid)
            terminal.println(green("The template was removed!"))
        }

    }

}

