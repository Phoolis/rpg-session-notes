package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.BaseRepository
import fi.paulcarlson.domain.campaign.CampaignId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.max
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.updateReturning

interface SessionRepository : BaseRepository {
    suspend fun save(session: Session): Session
    suspend fun findByCampaign(campaignId: CampaignId): List<Session>
    suspend fun findById(id: SessionId): Session?
    suspend fun update(session: Session): Session
    suspend fun delete(id: SessionId): Boolean
}

class DSLSessionRepository : SessionRepository {
    override suspend fun save(session: Session): Session = dbQuery {
        // Determine the next session number for this campaign
        val nextNumber = (Sessions
            .select(Sessions.sessionNumber.max())
            .where { Sessions.campaignId eq session.campaignId.value }
            .singleOrNull()?.getOrNull(Sessions.sessionNumber.max()) ?: 0) + 1

        val insertedRow = Sessions.insertReturning(
            returning = listOf(Sessions.id, Sessions.sessionNumber)
        ) {
            it[campaignId] = session.campaignId.value
            it[sessionNumber] = nextNumber
            it[sessionDate] = session.sessionDate
        }.single()

        Session(
            id = SessionId(insertedRow[Sessions.id]),
            campaignId = session.campaignId,
            sessionDate = session.sessionDate,
            sessionNumber = insertedRow[Sessions.sessionNumber]
        )
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

    override suspend fun update(session: Session): Session = dbQuery {
        requireNotNull(session.id) { "Missing or invalid session ID" }

        val updatedRows = Sessions.updateReturning(
            where = { Sessions.id eq session.id.value }
        ) {
            it[sessionDate] = session.sessionDate
        }

        updatedRows.singleOrNull()?.let(::rowToSession)
            ?: throw NoSuchElementException("Session not found")
    }

    override suspend fun delete(id: SessionId): Boolean = dbQuery {
        val rowsDeleted = Sessions.deleteWhere { Sessions.id eq id.value }
        rowsDeleted == 1
    }

}