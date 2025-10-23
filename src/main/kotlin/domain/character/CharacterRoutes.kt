package fi.paulcarlson.domain.character

import fi.paulcarlson.domain.campaign.CampaignRepository
import fi.paulcarlson.domain.user.UserRepository
import fi.paulcarlson.util.getUuidParam
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
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

suspend fun Application.characterRoutes() {
    val characterRepository = dependencies.resolve<CharacterRepository>()
    val campaignRepository = dependencies.resolve<CampaignRepository>()
    val userRepository = dependencies.resolve<UserRepository>()
    val characterService = CharacterService(characterRepository, campaignRepository, userRepository)

    routing {
        route("/characters") {
            post {
                val character = call.receive<Character>()
                val createdCharacter = characterService.createCharacter(character)
                call.respond(HttpStatusCode.OK, createdCharacter)
            }

            get("{id}") {
                val id = call.getUuidParam("id")

                val character = characterService.getCharacterById(id)
                    ?: throw NotFoundException("Character not found")
                call.respond(character)
            }

            get("/byCampaign/{campaignId}") {
                val campaignId = call.getUuidParam("campaignId")

                val campaignCharacters = characterService.getCharactersByCampaign(campaignId)
                call.respond(HttpStatusCode.OK, campaignCharacters)
            }

            get("/byUser/{userId}") {
                val userId = call.getUuidParam("userId")

                val userCharacters = characterService.getCharactersByUser(userId)
                call.respond(HttpStatusCode.OK, userCharacters)
            }

            put("{id}") {
                val id = call.getUuidParam("id")

                val character = call.receive<Character>()
                val updatedCharacter = characterService.editCharacter(character.copy(id = CharacterId(id)))
                call.respond(HttpStatusCode.OK, updatedCharacter)
            }

            delete("{id}") {
                val id = call.getUuidParam("id")

                characterService.removeCharacter(id)
                    .takeIf { it }
                    ?: throw NotFoundException("Character not found")
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}