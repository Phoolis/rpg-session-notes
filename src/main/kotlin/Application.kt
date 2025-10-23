package fi.paulcarlson

import fi.paulcarlson.domain.campaign.CampaignRepository
import fi.paulcarlson.domain.campaign.DSLCampaignRepository
import fi.paulcarlson.domain.campaign.FakeCampaignRepository
import fi.paulcarlson.domain.note.DSLNoteRepository
import fi.paulcarlson.domain.note.NoteRepository
import fi.paulcarlson.domain.session.DSLSessionRepository
import fi.paulcarlson.domain.session.SessionRepository
import fi.paulcarlson.domain.user.DSLUserRepository
import fi.paulcarlson.domain.user.UserRepository
import fi.paulcarlson.plugins.configureDatabase
import fi.paulcarlson.plugins.configureMonitoring
import fi.paulcarlson.plugins.configureRouting
import fi.paulcarlson.plugins.configureSerialization
import fi.paulcarlson.plugins.configureStatusPages
import fi.paulcarlson.plugins.configureValidation
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

suspend fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureDatabase()

    dependencies {
        provide<CampaignRepository> { DSLCampaignRepository() }
        provide<SessionRepository> { DSLSessionRepository() }
        provide<NoteRepository> { DSLNoteRepository() }
        provide<UserRepository> { DSLUserRepository() }
    }

    configureRouting()
    configureStatusPages()
    configureValidation()
}

// TODO: Move testModule into the test folder if possible
suspend fun Application.testModule() {
    configureMonitoring()
    configureSerialization()
    // configureDatabase() // Run tests in-memory for now

    dependencies {
        provide<CampaignRepository> { FakeCampaignRepository() }
        // TODO: Implement FakeSessionRepository
        // TODO: Implement FakeNoteRepository
    }

    configureRouting()
    configureStatusPages()
    configureValidation()
}
