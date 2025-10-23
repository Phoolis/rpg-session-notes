package fi.paulcarlson.plugins

import fi.paulcarlson.domain.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.uri
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    error = HttpStatusCode.BadRequest.description,
                    message = cause.message ?: "Bad request",
                    path = call.request.uri
                )
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    status = HttpStatusCode.NotFound.value,
                    error = HttpStatusCode.NotFound.description,
                    message = cause.message ?: "Resource not found",
                    path = call.request.uri
                )
            )
        }

        exception<RequestValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    error = HttpStatusCode.BadRequest.description,
                    message = cause.reasons.joinToString(),
                    path = call.request.uri
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    status = HttpStatusCode.InternalServerError.value,
                    error = HttpStatusCode.InternalServerError.description,
                    message = "Internal server error",
                    path = call.request.uri
                )
            )
        }
    }
}