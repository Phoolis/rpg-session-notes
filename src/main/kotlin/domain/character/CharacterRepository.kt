package fi.paulcarlson.domain.character

import fi.paulcarlson.domain.BaseRepository
import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.user.UserId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.updateReturning

interface CharacterRepository : BaseRepository {
    suspend fun save(character: Character): Character
    suspend fun findByCampaign(campaignId: CampaignId): List<Character>
    suspend fun findByUser(userId: UserId): List<Character>
    suspend fun findById(id: CharacterId): Character?
    suspend fun update(character: Character): Character
    suspend fun delete(id: CharacterId): Boolean
}

class DSLCharacterRepository : CharacterRepository {
    override suspend fun save(character: Character): Character = dbQuery {
        val insertedRow = Characters.insertReturning(
            returning = Characters.columns.toList()
        ) {
            it[campaignId] = character.campaignId.value
            it[userId] = character.userId.value
            it[name] = character.name
            it[role] = character.role
        }.single()

        rowToCharacter(insertedRow)
    }

    override suspend fun findByCampaign(campaignId: CampaignId): List<Character> = dbQuery {
        Characters
            .selectAll()
            .where { Characters.campaignId eq campaignId.value }
            .map(::rowToCharacter)
            .toList()
    }

    override suspend fun findByUser(userId: UserId): List<Character> = dbQuery {
        Characters
            .selectAll()
            .where { Characters.userId eq userId.value }
            .map(::rowToCharacter)
            .toList()
    }

    override suspend fun findById(id: CharacterId): Character? = dbQuery {
        Characters
            .selectAll()
            .where { Characters.id eq id.value }
            .singleOrNull()
            ?.let(::rowToCharacter)
    }

    override suspend fun update(character: Character): Character = dbQuery {
        requireNotNull(character.id) { "Missing or invalid character ID"}

        val updatedRow = Characters.updateReturning(
            where = { Characters.id eq character.id.value },
            returning = Characters.columns.toList()
        ) {
            it[name] = character.name
            it[role] = character.role
        }.single()

        rowToCharacter(updatedRow)
    }

    override suspend fun delete(id: CharacterId): Boolean = dbQuery {
        val rowsDeleted = Characters.deleteWhere { Characters.id eq id.value }
        rowsDeleted == 1
    }

}