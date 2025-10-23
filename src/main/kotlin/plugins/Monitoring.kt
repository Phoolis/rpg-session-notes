package fi.paulcarlson.plugins

import org.slf4j.event.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.path

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/")
        }
    }
}