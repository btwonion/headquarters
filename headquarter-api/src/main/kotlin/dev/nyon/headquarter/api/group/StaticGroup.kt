package dev.nyon.headquarter.api.group

import dev.nyon.headquarter.api.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

data class StaticGroup(
    override val uuid: @Serializable(with = UUIDSerializer::class) UUID,
    override var name: String,
    override var description: String,
    override var defaultTemplate: Template?,
    override var maxMemory: Int,
    override var maxRunningServices: Int,
    override var minRunningServices: Int,
    var runningHost: String,
    var runningPath: String
) : Group(
    uuid, name, description, defaultTemplate, true, maxMemory, maxRunningServices, minRunningServices
)