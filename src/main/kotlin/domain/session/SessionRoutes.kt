package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignRepository
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

suspend fun Application.sessionRoutes() {
    val sessionRepository = dependencies.resolve<SessionRepository>()
    val campaignRepository = dependencies.resolve<CampaignRepository>()
    val sessionService = SessionService(sessionRepository, campaignRepository)

    routing {
        route("/sessions") {
            post {
                val session = call.receive<Session>()
                val createdSession = sessionService.createSession(session)
                call.respond(HttpStatusCode.Created, createdSession)
            }

            get("{id}") {
                val id = call.getUuidParam("id")

                val session = sessionService.getSessionById(id)
                    ?: throw NotFoundException("Session not found")
                call.respond(session)
            }

            // TODO: Figure if this is a good place for the route for getting all sessions for a campaign

            get("/byCampaign/{campaignId}") {
                val campaignId = call.getUuidParam("campaignId")

                val campaignSessions = sessionService.getSessionsByCampaign(campaignId)
                call.respond(HttpStatusCode.OK, campaignSessions)
            }

            put("{id}") {
                val id = call.getUuidParam("id")

                val session = call.receive<Session>()
                val updatedSession = sessionService.editSession(session.copy(id = SessionId(id)))
                call.respond(HttpStatusCode.OK, updatedSession)
            }

            delete("{id}") {
                val id = call.getUuidParam("id")

                sessionService.removeSession(id)
                    .takeIf { it }
                    ?: throw NotFoundException("Session not found")
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}