package fi.paulcarlson.util

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.RoutingCall
import java.util.UUID

fun RoutingCall.getUuidParam(name: String): UUID {
    val param = parameters[name]
        ?: throw BadRequestException("Missing parameter $name")

    return try {
        UUID.fromString(param)
    } catch (ex: IllegalArgumentException) {
        throw BadRequestException("Invalid UUID format for $name")
    }
}