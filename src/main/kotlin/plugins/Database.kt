package fi.paulcarlson.plugins

import fi.paulcarlson.domain.campaign.Campaigns
import fi.paulcarlson.domain.note.Notes
import fi.paulcarlson.domain.session.Sessions
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.IsolationLevel
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

suspend fun Application.configureDatabase() {
    val config = environment.config.config("database")
    val dbName = config.property("name").getString()
    val dbUser = config.property("user").getString()
    val dbPassword = config.property("password").getString()

    val database = R2dbcDatabase.connect(
        url = "r2dbc:postgresql://localhost:5432/$dbName",
        databaseConfig = R2dbcDatabaseConfig {
            defaultMaxAttempts = 1
            defaultR2dbcIsolationLevel = IsolationLevel.READ_COMMITTED

            connectionFactoryOptions {
                option(ConnectionFactoryOptions.USER, dbUser)
                option(ConnectionFactoryOptions.PASSWORD, dbPassword)
            }
        }
    )

    log.info("Connecting to PostgreSQL at localhost:5432/$dbName as $dbUser")

    suspendTransaction(db = database) {
        SchemaUtils.drop(Campaigns, Sessions, Notes)
        SchemaUtils.create(Campaigns, Sessions, Notes)
    }

    log.info("Dropped and re-created tables: Campaigns, Sessions, Notes")
}