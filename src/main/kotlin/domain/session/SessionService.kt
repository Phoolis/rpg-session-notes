package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.campaign.CampaignRepository
import io.ktor.server.plugins.NotFoundException
import java.util.UUID

class SessionService(
    private val sessionRepository: SessionRepository,
    private val campaignRepository: CampaignRepository
) {
    suspend fun createSession(session: Session): Session {
        campaignRepository.findById(session.campaignId)
            ?: throw NotFoundException("Campaign not found")
        return sessionRepository.save(session)
    }

    suspend fun getSessionById(id: UUID): Session? {
        return sessionRepository.findById(SessionId(id))
    }

    suspend fun getSessionsByCampaign(campaignId: UUID): List<Session> {
        return sessionRepository.findByCampaign(CampaignId(campaignId))
    }

    suspend fun editSession(session: Session): Session {
        val existing = sessionRepository.findById(session.id!!)
            ?: throw NotFoundException("Session not found")

        // Only allow changing the session date
        val updated = existing.copy(
            sessionDate = session.sessionDate
        )

        return sessionRepository.update(updated)
    }

    suspend fun removeSession(id: UUID): Boolean {
        return sessionRepository.delete(SessionId(id))
    }
}