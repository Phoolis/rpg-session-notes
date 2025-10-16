package fi.paulcarlson

import fi.paulcarlson.repository.CampaignRepository
import fi.paulcarlson.model.Campaign
import io.ktor.http.*
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(repository: CampaignRepository) {
        install(ContentNegotiation) {
      json()
    }
    routing {
        route("/campaigns") {

            get {
                val campaigns = repository.allCampaigns()
                call.respond(campaigns)
            }

            get("/byId/{campaignId}") {
                val id = call.parameters["campaignId"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val campaign = repository.campaignById(id)
                if (campaign == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                call.respond(campaign)
            }

            post {
                try {
                    val campaign = call.receive<Campaign>()
                    repository.addCampaign(campaign)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{campaignId}") {
                val id = call.parameters["campaignId"]?.toLongOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if (repository.removeCampaign(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
