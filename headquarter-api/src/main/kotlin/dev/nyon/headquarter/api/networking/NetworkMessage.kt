package dev.nyon.headquarter.api.networking

import dev.nyon.headquarter.api.common.UUIDSerializer
import dev.nyon.headquarter.api.distribution.Client
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
sealed class NetworkMessage

/**
 * Modifies, Creates, Deletes a service stored in the internal server's database.
 * Just insert for service the new [client][Client] object
 * The uuid is only necessary for deleting
 * @param service the service to modify
 * @param uuid the uuid of the service
 */
@Serializable
data class ServiceModify(val service: Client?, val uuid: @Serializable(with = UUIDSerializer::class) UUID?) :
    NetworkMessage()

/**
 * Requests a service from the internal server's database
 * @param uuid the uuid of the service
 */
@Serializable
data class ServiceRequest(val uuid: @Serializable(with = UUIDSerializer::class) UUID) : NetworkMessage()

/**
 * The answer packet to the [request][ServiceRequest] packet
 * @param service the requested service
 */
@Serializable
data class ServiceRequestAnswer(val service: Client?) : NetworkMessage()