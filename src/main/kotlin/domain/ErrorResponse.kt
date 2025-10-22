package fi.paulcarlson.domain

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: Instant = Clock.System.now() // Instant.now() is deprecated since Kotlin 2.1
)