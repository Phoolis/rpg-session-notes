package fi.paulcarlson.plugins

import fi.paulcarlson.domain.campaign.campaignRoutes
import fi.paulcarlson.domain.character.characterRoutes
import fi.paulcarlson.domain.note.noteRoutes
import fi.paulcarlson.domain.session.sessionRoutes
import fi.paulcarlson.domain.user.userRoutes
import io.ktor.server.application.*

suspend fun Application.configureRouting() {
    campaignRoutes()
    sessionRoutes()
    noteRoutes()
    userRoutes()
    characterRoutes()
}
