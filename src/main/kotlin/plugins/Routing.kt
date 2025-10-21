package fi.paulcarlson.plugins

import fi.paulcarlson.domain.campaign.campaignRoutes
import fi.paulcarlson.domain.note.noteRoutes
import fi.paulcarlson.domain.session.sessionRoutes
import io.ktor.server.application.*

suspend fun Application.configureRouting() {
    campaignRoutes()
    sessionRoutes()
    noteRoutes()
}
