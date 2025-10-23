package fi.paulcarlson.domain.user

import fi.paulcarlson.util.getUuidParam
import io.ktor.http.HttpStatusCode
import io.ktor.http.decodeURLPart
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

suspend fun Application.userRoutes() {
    val userRepository = dependencies.resolve<UserRepository>()
    val userService = UserService(userRepository)

    routing {
        route("/users") {
            post {
                val user = call.receive<User>()
                val createdUser = userService.createUser(user)
                call.respond(HttpStatusCode.Created, createdUser)
            }

            get {
                val allUsers = userService.getUsers()
                call.respond(HttpStatusCode.OK, allUsers)
            }

            get("{id}") {
                val id = call.getUuidParam("id")

                val user = userService.getUserById(id)
                    ?: throw NotFoundException("User not found")
                call.respond(HttpStatusCode.OK, user)
            }

            get("/email/{email}") {
                val emailParam = call.parameters["email"]
                    ?: throw BadRequestException("Missing email in request path")
                val email = emailParam.decodeURLPart() // Decode %40 to @ etc.

                if (!email.contains("@"))
                    throw BadRequestException("Invalid email format")

                val user = userService.getUserByEmail(email)
                    ?: throw NotFoundException("User not found with email: $email")
                call.respond(HttpStatusCode.OK, user)
            }

            put("{id}") {
                val id = call.getUuidParam("id")

                val user = call.receive<User>()
                val updated = userService.editUser(user.copy(id = UserId(id)))
                call.respond(HttpStatusCode.OK, updated)
            }

            delete("{id}") {
                val id = call.getUuidParam("id")

                userService.removeUser(id)
                    .takeIf { it }
                    ?: throw NotFoundException("User not found")
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}