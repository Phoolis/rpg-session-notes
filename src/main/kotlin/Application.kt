package fi.paulcarlson

import com.auth0.jwk.JwkProviderBuilder
import fi.paulcarlson.domain.campaign.CampaignRepository
import fi.paulcarlson.domain.campaign.DSLCampaignRepository
import fi.paulcarlson.domain.campaign.FakeCampaignRepository
import fi.paulcarlson.domain.character.CharacterRepository
import fi.paulcarlson.domain.character.DSLCharacterRepository
import fi.paulcarlson.domain.note.DSLNoteRepository
import fi.paulcarlson.domain.note.NoteRepository
import fi.paulcarlson.domain.security.JwtService
import fi.paulcarlson.domain.session.DSLSessionRepository
import fi.paulcarlson.domain.session.SessionRepository
import fi.paulcarlson.domain.user.DSLUserRepository
import fi.paulcarlson.domain.user.UserRepository
import fi.paulcarlson.domain.user.UserService
import fi.paulcarlson.plugins.configureAuthentication
import fi.paulcarlson.plugins.configureDatabase
import fi.paulcarlson.plugins.configureMonitoring
import fi.paulcarlson.plugins.configureRouting
import fi.paulcarlson.plugins.configureSerialization
import fi.paulcarlson.plugins.configureStatusPages
import fi.paulcarlson.plugins.configureValidation
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import java.util.concurrent.TimeUnit

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
        provide<CharacterRepository> { DSLCharacterRepository() }
    }

    // Please read the jwt property from the config file if you are using EngineMain
    // TODO: Generate new rs256 key and move it to .env
    val jwtConfig = environment.config.config("jwt")
    val issuer = jwtConfig.property("issuer").getString()
    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    val userService = UserService(dependencies.resolve())
    val jwtService = JwtService(jwtConfig, jwkProvider, userService)

    configureAuthentication(jwtService, jwkProvider, issuer)
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
