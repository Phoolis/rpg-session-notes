package fi.paulcarlson.domain.note

import fi.paulcarlson.domain.session.SessionId
import fi.paulcarlson.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Instant

@JvmInline
@Serializable
value class NoteId(
    @Serializable(with = UUIDSerializer::class)
    val value: UUID
)

@Serializable
data class Note(
    val id: NoteId? = null,
    val sessionId: SessionId,
    val content: String,
    val authorName: String,

    // Disable kotlin.time.Instant Experimental warnings:
    // Declare opt-in in build.gradle.kts compilerOptions
    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null,
)