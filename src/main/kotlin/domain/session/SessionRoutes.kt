package fi.paulcarlson.domain.session

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

suspend fun Application.sessionRoutes() {
    val sessionRepository = dependencies.resolve<SessionRepository>()
    val sessionService = SessionService(sessionRepository)

    routing {
        route("/sessions") {
            post {
                val session = call.receive<Session>()
                val createdSession = sessionService.createSession(session)
                call.respond(HttpStatusCode.Created, createdSession)
            }

            get("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@get call.respondText("Missing session ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@get call.respondText("Invalid session ID format", status = HttpStatusCode.BadRequest)
                }

                val session = sessionService.getSessionById(id)
                    ?: return@get call.respondText("No session found with ID $id", status = HttpStatusCode.NotFound)
                call.respond(session)
            }

            // TODO: Figure if this is a good place for the route for getting all sessions for a campaign

            get("/byCampaign/{campaignId}") {
                val idParam = call.parameters["campaignId"]
                    ?: return@get call.respondText("Missing campaign ID", status = HttpStatusCode.BadRequest)

                val campaignId = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@get call.respondText("Invalid campaign ID format", status = HttpStatusCode.BadRequest)
                }

                val campaignSessions = sessionService.getSessionsByCampaign(campaignId)
                call.respond(HttpStatusCode.OK, campaignSessions)
            }

            put("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@put call.respondText("Missing session ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@put call.respondText("Invalid session ID format", status = HttpStatusCode.BadRequest)
                }

                try {
                    val session = call.receive<Session>()
                    val updatedSession = sessionService.editSession(session)
                    call.respond(HttpStatusCode.OK, updatedSession)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, ex.message ?: "Bad request")
                } catch (ex: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound, ex.message ?: "Session not found")
                } catch (ex: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unexpected error")
                }
            }

            delete("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@delete call.respondText("Missing session ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@delete call.respondText("Invalid session ID format", status = HttpStatusCode.BadRequest)
                }

                val success = sessionService.removeSession(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respondText("Session with ID = $id not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}