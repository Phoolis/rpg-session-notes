package fi.paulcarlson.domain.note

import fi.paulcarlson.domain.session.SessionId
import io.ktor.server.plugins.NotFoundException
import java.util.UUID

class NoteService(
    private val noteRepository: NoteRepository
) {
    suspend fun createNote(note: Note): Note {
        return noteRepository.save(note)
    }

    suspend fun getNoteById(id: UUID): Note? {
        return noteRepository.findById(NoteId(id))
    }

    suspend fun getNotesBySession(sessionId: UUID): List<Note> {
        return noteRepository.findBySession(SessionId(sessionId))
    }

    suspend fun editNote(note: Note): Note {
        val existing = noteRepository.findById(note.id!!)
            ?: throw NotFoundException("Note not found")

        // Only allow content changes
        val updated = existing.copy(
            content = note.content
            // authorName, sessionId, createdAt stays as they were in the db
        )

        return noteRepository.update(updated)
    }

    suspend fun removeNote(id: UUID): Boolean {
        return noteRepository.delete(NoteId(id))
    }
}