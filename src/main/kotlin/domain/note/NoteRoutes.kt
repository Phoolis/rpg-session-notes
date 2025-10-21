package fi.paulcarlson.domain.note

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
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
    val noteService = NoteService(noteRepository)

    routing {
        route("/notes") {
            post {
                val note = call.receive<Note>()
                val createdNote = noteService.createNote(note)
                call.respond(HttpStatusCode.OK, createdNote)
            }

            get("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@get call.respondText("Missing note ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@get call.respondText("Invalid note ID format", status = HttpStatusCode.BadRequest)
                }

                val note = noteService.getNoteById(id)
                    ?: return@get call.respondText("No note found with ID $id", status = HttpStatusCode.NotFound)
                call.respond(note)
            }

            get("/bySession/{sessionId}") {
                val idParam = call.parameters["sessionId"]
                    ?: return@get call.respondText("Missing session ID", status = HttpStatusCode.BadRequest)

                val sessionId = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@get call.respondText("Invalid session ID format", status = HttpStatusCode.BadRequest)
                }

                val sessionNotes = noteService.getNotesBySession(sessionId)
                call.respond(HttpStatusCode.OK, sessionNotes)
            }

            put("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@put call.respondText("Missing note ID", status = HttpStatusCode.BadRequest)

                try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@put call.respondText("Invalid note ID format", status = HttpStatusCode.BadRequest)
                }

                try {
                    val note = call.receive<Note>()
                    val updatedNote = noteService.editNote(note)
                    call.respond(HttpStatusCode.OK, updatedNote)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "Bad request")
                } catch (ex: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, ex.message ?: "Note not found")
                } catch (ex: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unexpected error")
                }
            }

            delete("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@delete call.respondText("Missing note ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@delete call.respondText("Invalid note ID format", status = HttpStatusCode.BadRequest)
                }

                val success = noteService.removeNote(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respondText("Note with ID = $id not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}