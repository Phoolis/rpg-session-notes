package fi.paulcarlson

import fi.paulcarlson.domain.campaign.CampaignRepository
import fi.paulcarlson.domain.campaign.DSLCampaignRepository
import fi.paulcarlson.domain.campaign.FakeCampaignRepository
import fi.paulcarlson.domain.session.DSLSessionRepository
import fi.paulcarlson.domain.session.SessionRepository
import fi.paulcarlson.plugins.configureDatabase
import fi.paulcarlson.plugins.configureRouting
import fi.paulcarlson.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

suspend fun Application.module() {
    configureSerialization()
    configureDatabase()

    dependencies {
        provide<CampaignRepository> { DSLCampaignRepository() }
        provide<SessionRepository> { DSLSessionRepository() }
    }

    configureRouting()
}

// Test module skips the DB setup. I'll run tests in-memory for now
suspend fun Application.testModule() {
    configureSerialization()

    dependencies {
        provide<CampaignRepository> { FakeCampaignRepository() }
        // TODO: Implement FakeSessionRepository
    }

    configureRouting()
}
