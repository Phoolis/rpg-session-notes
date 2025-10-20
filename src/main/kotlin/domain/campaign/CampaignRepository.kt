package fi.paulcarlson.domain.campaign

import fi.paulcarlson.domain.BaseRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.updateReturning

interface CampaignRepository : BaseRepository {
    suspend fun save(campaign: Campaign): CampaignId
    suspend fun findAll(): List<Campaign>
    suspend fun findById(id: CampaignId): Campaign?
    suspend fun update(campaign: Campaign): Campaign
    suspend fun delete(id: CampaignId): Boolean
}

class DSLCampaignRepository : CampaignRepository {
    override suspend fun save(campaign: Campaign): CampaignId = dbQuery {
        val id = Campaigns.insert {
            it[name] = campaign.name
            it[description] = campaign.description
        } get Campaigns.id

        CampaignId(id)
    }

    override suspend fun findAll(): List<Campaign> = dbQuery {
        Campaigns
            .selectAll()
            .map(::rowToCampaign)
            .toList()
    }

    override suspend fun findById(id: CampaignId): Campaign? = dbQuery {
        Campaigns
            .selectAll()
            .where { Campaigns.id eq id.value }
            .singleOrNull()
            ?.let(::rowToCampaign)
    }

    override suspend fun update(campaign: Campaign): Campaign = dbQuery {
        requireNotNull(campaign.id) { "Missing or invalid campaign ID" }

        // Here we are not returning any database generated columns, but using updateReturning still saves
        // us a round-trip to the DB - no need to select the updated record after.
        val updatedRows = Campaigns.updateReturning(
            where = { Campaigns.id eq campaign.id.value }
        ) {
            it[name] = campaign.name
            it[description] = campaign.description
        }

        updatedRows.singleOrNull()?.let(::rowToCampaign)
            ?: throw NoSuchElementException("Campaign not found")

    }

    override suspend fun delete(id: CampaignId): Boolean = dbQuery {
        val rowsDeleted = Campaigns.deleteWhere { Campaigns.id eq id.value }
        rowsDeleted == 1
    }
}