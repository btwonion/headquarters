package dev.nyon.headquarter.api.group

import dev.nyon.headquarter.api.common.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
open class Group(
    open val uuid: @Serializable(with = UUIDSerializer::class) UUID,
    open var name: String,
    open var description: String,
    open var defaultTemplate: Template?,
    var static: Boolean,
    open var maxMemory: Int,
    open var maxRunningServices: Int,
    open var minRunningServices: Int
)