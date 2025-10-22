package fi.paulcarlson.domain.user

import fi.paulcarlson.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Instant

@JvmInline
@Serializable
value class UserId(
    @Serializable(with = UUIDSerializer::class)
    val value: UUID
)

data class User(
    val id: UserId? = null,
    val username: String,
    val email: String,
    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null
)