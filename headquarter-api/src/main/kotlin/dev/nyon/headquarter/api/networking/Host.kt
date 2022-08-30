package dev.nyon.headquarter.api.networking

import kotlinx.serialization.Serializable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Serializable
data class Host(val ip: String, val port: Int?)

fun Host.Companion.determine(): Host {
    val ip = BufferedReader(
        InputStreamReader(
            URL(
                "http://checkip.amazonaws.com"
            ).openStream()
        )
    ).readText()

    return Host(ip, null)
}