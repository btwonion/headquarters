package dev.nyon.headquarter.api.distribution

import dev.nyon.headquarter.api.networking.Host
import java.util.UUID

interface RunningService {

    val host: Host
    val uuid: UUID
    val displayName: String

}