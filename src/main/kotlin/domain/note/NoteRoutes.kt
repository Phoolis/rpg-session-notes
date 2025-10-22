package fi.paulcarlson.domain.note

import fi.paulcarlson.domain.session.SessionRepository
import fi.paulcarlson.util.getUuidParam
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.NoSuchElementException
import java.util.UUID

suspend fun Application.noteRoutes() {
    val noteRepository = dependencies.resolve<NoteRepository>()
    val sessionRepository = dependencies.resolve<SessionRepository>()
    val noteService = NoteService(noteRepository, sessionRepository)

    routing {
        route("/notes") {
            post {
                val note = call.receive<Note>()
                val createdNote = noteService.createNote(note)
                call.respond(HttpStatusCode.OK, createdNote)
            }

            get("{id}") {
                val id = call.getUuidParam("id")

                val note = noteService.getNoteById(id)
                    ?: throw NotFoundException("Note not found")
                call.respond(note)
            }

            get("/bySession/{sessionId}") {
                val sessionId = call.getUuidParam("sessionId")

                val sessionNotes = noteService.getNotesBySession(sessionId)
                call.respond(HttpStatusCode.OK, sessionNotes)
            }

            put("{id}") {
                val id = call.getUuidParam("id")

                val note = call.receive<Note>()
                val updatedNote = noteService.editNote(note.copy(id = NoteId(id)))
                call.respond(HttpStatusCode.OK, updatedNote)
            }

            delete("{id}") {
                val id = call.getUuidParam("id")

                noteService.removeNote(id)
                    .takeIf { it }
                    ?: throw NotFoundException("Note not found")
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}