package fi.paulcarlson.plugins

import com.auth0.jwk.JwkProvider
import fi.paulcarlson.domain.security.JwtService
import fi.paulcarlson.domain.user.User
import fi.paulcarlson.domain.user.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File
import java.util.UUID

fun Application.configureAuthentication(
    jwtService: JwtService,
    jwkProvider: JwkProvider,
    issuer: String,
    userService: UserService
) {

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtService.realm
            verifier(jwkProvider, issuer)

            validate { credential ->
                jwtService.customValidator(credential)
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

    routing {
        post("/login") {
            val user = call.receive<User>()
            // TODO: OAuth here?
            val existingUser = userService.getUserByEmail(user.email)
                ?: throw NotFoundException("User not found")
            val token = jwtService.createJwtToken(existingUser)
            call.respond(mapOf("token" to token))
        }

        authenticate("auth-jwt") {
            get("/hello") {
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $email! Token is expired at $expiresAt ms.")
            }
        }
        // The public key lives in the certs-directory under root, and the public key is defined in jwks.json.
        // For more info: https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-set-properties
        staticFiles(".well-known", File("certs"), "jwks.json")
    }
}