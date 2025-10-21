package fi.paulcarlson.domain.note

import fi.paulcarlson.domain.session.SessionId
import fi.paulcarlson.domain.session.Sessions
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object Notes : Table() {
    val id = uuid("id").autoGenerate()
    val sessionId = reference(
        name = "session_id",
        refColumn = Sessions.id,
        onDelete = ReferenceOption.CASCADE
    )
    val authorName = varchar("author_name", 50)
    val content = text("content")

    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val modifiedAt = timestamp("modified_at").nullable() // No value on insert

    override val primaryKey = PrimaryKey(id)
}

fun rowToNote(result: ResultRow): Note = Note(
    id = NoteId(result[Notes.id]),
    sessionId = SessionId(result[Notes.sessionId]),
    authorName = result[Notes.authorName],
    content = result[Notes.content],
    createdAt = result[Notes.createdAt],
    modifiedAt = result[Notes.modifiedAt]
)