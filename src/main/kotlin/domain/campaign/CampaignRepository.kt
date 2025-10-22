package fi.paulcarlson.domain.campaign

import fi.paulcarlson.domain.BaseRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.updateReturning

interface CampaignRepository : BaseRepository {
    suspend fun save(campaign: Campaign): Campaign
    suspend fun findAll(): List<Campaign>
    suspend fun findById(id: CampaignId): Campaign?
    suspend fun update(campaign: Campaign): Campaign
    suspend fun delete(id: CampaignId): Boolean
}

class DSLCampaignRepository : CampaignRepository {
    override suspend fun save(campaign: Campaign): Campaign = dbQuery {
        val insertedRow = Campaigns.insertReturning(
            returning = Campaigns.columns.toList()
        ) {
            it[name] = campaign.name
            it[description] = campaign.description
        }.single()

        rowToCampaign(insertedRow)
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

        // Here we are returning all the columns from DB upon updating the record.
        // Saves us a round-trip to the DB - no need to select the updated record afterward.
        // Possible at least in Postgres.
        val updatedRow = Campaigns.updateReturning(
            where = { Campaigns.id eq campaign.id.value },
            returning = Campaigns.columns.toList()
        ) {
            it[name] = campaign.name
            it[description] = campaign.description
            it[modifiedAt] = CurrentTimestamp
        }.single()

        rowToCampaign(updatedRow)
    }

    override suspend fun delete(id: CampaignId): Boolean = dbQuery {
        val rowsDeleted = Campaigns.deleteWhere { Campaigns.id eq id.value }
        rowsDeleted == 1
    }
}