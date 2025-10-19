package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignId
import java.util.UUID

class SessionService(
    private val sessionRepository: SessionRepository
) {
    suspend fun createSession(session: Session): Session {
        val id = sessionRepository.save(session)
        return session.copy(id = id)
    }

    suspend fun getSessionById(id: UUID): Session? {
        return sessionRepository.findById(SessionId(id))
    }

    suspend fun getSessionsByCampaign(id: UUID): List<Session> {
        return sessionRepository.findByCampaign(CampaignId(id))
    }

    suspend fun removeSession(id: UUID): Boolean {
        return sessionRepository.delete(SessionId(id))
    }
}