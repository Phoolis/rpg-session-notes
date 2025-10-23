package fi.paulcarlson.domain.character

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.campaign.CampaignRepository
import fi.paulcarlson.domain.user.UserId
import fi.paulcarlson.domain.user.UserRepository
import io.ktor.server.plugins.NotFoundException
import java.util.UUID

class CharacterService(
    private val characterRepository: CharacterRepository,
    private val campaignRepository: CampaignRepository,
    private val userRepository: UserRepository
) {
    suspend fun createCharacter(character: Character): Character {
        campaignRepository.findById(character.campaignId)
            ?: throw NotFoundException("Campaign not found")
        userRepository.findById(character.userId)
            ?: throw NotFoundException("User not found")
        return characterRepository.save(character)
    }

    suspend fun getCharacterById(id: UUID): Character? {
        return characterRepository.findById(CharacterId(id))
    }

    suspend fun getCharactersByCampaign(campaignId: UUID): List<Character> {
        campaignRepository.findById(CampaignId(campaignId))
            ?: throw NotFoundException("Campaign not found")
        return characterRepository.findByCampaign(CampaignId(campaignId))
    }

    suspend fun getCharactersByUser(userId: UUID): List<Character> {
        userRepository.findById(UserId(userId))
            ?: throw NotFoundException("User not found")
        return characterRepository.findByUser(UserId(userId))
    }

    suspend fun editCharacter(character: Character): Character {
        val existing = characterRepository.findById(character.id!!)
            ?: throw NotFoundException("Character not found")

        val updated = existing.copy(
            name = character.name,
            role = character.role
        )

        return characterRepository.update(updated)
    }

    suspend fun removeCharacter(id: UUID): Boolean {
        return characterRepository.delete(CharacterId(id))
    }
}