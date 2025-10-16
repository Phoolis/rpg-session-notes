package fi.paulcarlson.model

import kotlinx.serialization.Serializable

@Serializable
data class Campaign(
    val id: Long,
    val name: String,
    val description: String,
)