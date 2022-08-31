package dev.nyon.headquarter.node.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import dev.nyon.headquarter.api.group.Group
import dev.nyon.headquarter.api.group.Template
import dev.nyon.headquarter.node.groupCache
import dev.nyon.headquarter.node.templateCache

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
        val choices = mutableMapOf<String, Template>()
        templateCache.forEach {
            choices[it.value.name] = it.value
        }
        return@run choices
    })

fun CliktCommand.templateArgument(description: String) = argument("template", help = description).choice(kotlin.run {
    val choices = mutableMapOf<String, Template>()
    templateCache.forEach {
        choices[it.value.name] = it.value
    }
    return@run choices
})