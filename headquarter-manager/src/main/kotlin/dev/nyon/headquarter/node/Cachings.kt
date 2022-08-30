package dev.nyon.headquarter.node

import dev.nyon.headquarter.api.database.groups
import dev.nyon.headquarter.api.database.templates
import dev.nyon.headquarter.api.group.Group
import dev.nyon.headquarter.api.group.Template
import java.util.*

val templateCache = mutableMapOf<UUID, Template>()
val groupCache = mutableMapOf<UUID, Group>()

suspend fun loadCaches() {
    templates.find().toFlow().collect {
        templateCache[it.uuid] = it
    }

    groups.find().toFlow().collect {
        groupCache[it.uuid] = it
    }
}