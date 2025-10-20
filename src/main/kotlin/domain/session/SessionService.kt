package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignId
import java.util.UUID

class SessionService(
    private val sessionRepository: SessionRepository
) {
    suspend fun createSession(session: Session): Session {
        return sessionRepository.save(session)
    }

    suspend fun getSessionById(id: UUID): Session? {
        return sessionRepository.findById(SessionId(id))
    }

    suspend fun getSessionsByCampaign(id: UUID): List<Session> {
        return sessionRepository.findByCampaign(CampaignId(id))
    }

    suspend fun editSession(session: Session): Session {
        return sessionRepository.update(session)
    }

    suspend fun removeSession(id: UUID): Boolean {
        return sessionRepository.delete(SessionId(id))
    }
}