package fi.paulcarlson.domain.campaign

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

suspend fun Application.campaignRoutes() {
    val campaignRepository = dependencies.resolve<CampaignRepository>()
    val campaignService = CampaignService(campaignRepository)

    routing {
        route("/campaigns") {
            post {
                val campaign = call.receive<Campaign>()
                val createdCampaign = campaignService.createCampaign(campaign)
                call.respond(HttpStatusCode.Created, createdCampaign)
            }

            put("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@put call.respondText("Missing campaign ID", status = HttpStatusCode.BadRequest)

                try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@put call.respondText("Invalid campaign ID format", status = HttpStatusCode.BadRequest)
                }

                val campaign = call.receive<Campaign>()
                val updatedCampaign = campaignService.editCampaign(campaign)
                call.respond(HttpStatusCode.OK, updatedCampaign)
            }

            get {
                val allCampaigns = campaignService.getCampaigns()
                call.respond(HttpStatusCode.OK, allCampaigns)
            }

            get("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@get call.respondText("Missing campaign ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@get call.respondText("Invalid campaign ID format", status = HttpStatusCode.BadRequest)
                }

                val campaign = campaignService.getCampaign(id)
                    ?: return@get call.respondText("No campaign found with ID $id", status = HttpStatusCode.NotFound)
                call.respond(campaign)
            }


            delete("{id}") {
                val idParam = call.parameters["id"]
                    ?: return@delete call.respondText("Missing campaign ID", status = HttpStatusCode.BadRequest)

                val id = try {
                    UUID.fromString(idParam)
                } catch (ex: IllegalArgumentException) {
                    return@delete call.respondText("Invalid campaign ID format", status = HttpStatusCode.BadRequest)
                }
                val success = campaignService.removeCampaign(id)
                if (success) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respondText("Campaign with ID = $id not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}