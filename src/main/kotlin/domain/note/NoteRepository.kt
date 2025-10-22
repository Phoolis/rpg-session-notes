package fi.paulcarlson.domain.note

import fi.paulcarlson.domain.BaseRepository
import fi.paulcarlson.domain.session.SessionId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.updateReturning

interface NoteRepository : BaseRepository {
    suspend fun save(note: Note): Note
    suspend fun findBySession(sessionId: SessionId): List<Note>
    suspend fun findById(id: NoteId): Note?
    suspend fun update(note: Note): Note
    suspend fun delete(id: NoteId): Boolean
}

class DSLNoteRepository : NoteRepository {
    override suspend fun save(note: Note): Note = dbQuery {
        val insertedRow = Notes.insertReturning(
            returning = Notes.columns.toList()
        ) {
            it[sessionId] = note.sessionId.value
            it[content] = note.content
            it[authorName] = note.authorName
        }.single()

        rowToNote(insertedRow)
    }

    override suspend fun findBySession(sessionId: SessionId): List<Note> = dbQuery {
        Notes
            .selectAll()
            .where { Notes.sessionId eq sessionId.value }
            .orderBy(Notes.createdAt to SortOrder.ASC)
            .map(::rowToNote)
            .toList()
    }

    override suspend fun findById(id: NoteId): Note? = dbQuery {
        Notes
            .selectAll()
            .where { Notes.id eq id.value }
            .singleOrNull()
            ?.let(::rowToNote)
    }

    override suspend fun update(note: Note): Note = dbQuery {
        requireNotNull(note.id) { "Missing or invalid note ID"}

        val updatedRow = Notes.updateReturning(
            where = { Notes.id eq note.id.value },
            returning = Notes.columns.toList()
        ) {
            it[content] = note.content
            it[modifiedAt] = CurrentTimestamp
        }.single()

        rowToNote(updatedRow)
    }

    override suspend fun delete(id: NoteId): Boolean = dbQuery {
        val rowsDeleted = Notes.deleteWhere { Notes.id eq id.value }
        rowsDeleted == 1
    }

}