package fi.paulcarlson

import fi.paulcarlson.repository.FakeCampaignRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = FakeCampaignRepository()

    configureSerialization(repository)
    configureDatabases()
    configureRouting()
}
