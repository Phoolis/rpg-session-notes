package fi.paulcarlson.domain.campaign


import fi.paulcarlson.util.getUuidParam
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

suspend fun Application.campaignRoutes() {
    val campaignRepository = dependencies.resolve<CampaignRepository>()
    val campaignService = CampaignService(campaignRepository)

    routing {
        route("/campaigns") {
            post {
                val campaign = call.receive<Campaign>() // Malformed requests are caught by StatusPages as 400
                // TODO: Add validation: eg. campaign name max 50 char, since these lead to database errors.
                val createdCampaign = campaignService.createCampaign(campaign)
                call.respond(HttpStatusCode.Created, createdCampaign)
            }


            get {
                val allCampaigns = campaignService.getCampaigns()
                call.respond(HttpStatusCode.OK, allCampaigns)
            }

            get("{id}") {
                val id = call.getUuidParam("id")

                val campaign = campaignService.getCampaign(id)
                    ?: throw NotFoundException("Campaign not found")
                call.respond(HttpStatusCode.OK, campaign)
            }

            put("{id}") {
                val id = call.getUuidParam("id")

                val campaign = call.receive<Campaign>()
                val updated = campaignService.editCampaign(campaign.copy(id = CampaignId(id)))
                call.respond(HttpStatusCode.OK, updated)
            }

            delete("{id}") {
                val id = call.getUuidParam("id")

                // A bit of Kotlin syntactic sugar:
                // takeIf { it } returns the boolean if true, otherwise null, which triggers the Elvis operator to throw.
                campaignService.removeCampaign(id)
                    .takeIf { it }
                    ?: throw NotFoundException("Campaign not found")
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
