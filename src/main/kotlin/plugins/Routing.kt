package fi.paulcarlson.plugins

import fi.paulcarlson.domain.campaign.campaignRoutes
import io.ktor.server.application.*

suspend fun Application.configureRouting() {
    campaignRoutes()
}
