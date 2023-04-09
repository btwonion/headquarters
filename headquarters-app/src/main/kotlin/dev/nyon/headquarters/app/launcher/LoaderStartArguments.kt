package dev.nyon.headquarters.app.launcher

import dev.nyon.headquarters.app.arch
import dev.nyon.headquarters.app.os
import dev.nyon.headquarters.app.database.models.Profile
import dev.nyon.headquarters.connector.mojang.models.`package`.Argument
import dev.nyon.headquarters.connector.mojang.models.`package`.RuleAction

fun MutableList<String>.addFabricArguments(profile: Profile) {
    addAll(profile.loaderProfile!!.arguments.jvm ?: listOf())
    add(profile.loaderProfile!!.mainClass)
}

fun MutableList<String>.addQuiltArguments(profile: Profile) {
    add(profile.loaderProfile!!.mainClass)
}

fun MutableList<String>.addVanillaArguments(profile: Profile, jvmLibsCompletedCallback: () -> Unit) {
    fun addConditionalArguments(list: List<Argument>) {
        list.forEach { argument ->
            when (argument) {
                is Argument.SimpleArgument -> {
                    add(argument.value)
                }

                is Argument.ExtendedArgument -> {
                    if (argument.rules.all { rule ->
                            (rule.action == RuleAction.Allow && rule.os?.all {
                                it.key == "arch" && it.value == arch || it.key == "name" && os.name.startsWith(
                                    it.value,
                                    ignoreCase = true
                                )
                            } == true)
                        })
                        addAll(argument.value)
                }
            }
        }
    }

    add("-Xmx${profile.memory}G")
    addAll(profile.extraJvmArgs)
    addConditionalArguments(profile.minecraftVersion!!.arguments.jvm)
    add(profile.minecraftVersion!!.logging.client!!.argument)
    jvmLibsCompletedCallback()
    addConditionalArguments(profile.minecraftVersion!!.arguments.game)
    addAll(profile.extraGameStartArgs)
}