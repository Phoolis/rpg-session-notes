package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.BaseRepository
import fi.paulcarlson.domain.campaign.CampaignId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

interface SessionRepository : BaseRepository {
    suspend fun save(session: Session): SessionId
    suspend fun findByCampaign(campaignId: CampaignId): List<Session>
    suspend fun findById(id: SessionId): Session?
    suspend fun delete(id: SessionId): Boolean
}

class DSLSessionRepository : SessionRepository {
    override suspend fun save(session: Session): SessionId = dbQuery {
        if (session.id == null) {
            // Determine the next session number for this campaign
            val nextNumber = (Sessions
                .select(Sessions.sessionNumber.max())
                .where { Sessions.campaignId eq session.campaignId.value }
                .singleOrNull()?.getOrNull(Sessions.sessionNumber.max()) ?: 0) + 1

            val id = Sessions.insert {
                it[campaignId] = session.campaignId.value
                it[sessionNumber] = nextNumber
                it[date] = session.date
            } get Sessions.id

            SessionId(id)
        } else {
            Sessions.update( { Sessions.id eq session.id.value }) {
                it[date] = session.date // Only allow updating the session date
            }
            session.id
        }
    }

    override suspend fun findByCampaign(campaignId: CampaignId): List<Session> = dbQuery {
        Sessions
            .selectAll()
            .where { Sessions.campaignId eq campaignId.value }
            .orderBy(Sessions.sessionNumber to SortOrder.ASC)
            .map(::rowToSession)
            .toList()
    }

    override suspend fun findById(id: SessionId): Session? = dbQuery {
        Sessions
            .selectAll()
            .where { Sessions.id eq id.value }
            .singleOrNull()
            ?.let(::rowToSession)
    }

    override suspend fun delete(id: SessionId): Boolean = dbQuery {
        val rowsDeleted = Sessions.deleteWhere { Sessions.id eq id.value }
        rowsDeleted == 1
    }

}