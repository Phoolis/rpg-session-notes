package fi.paulcarlson.domain.campaign

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class FakeCampaignRepository: CampaignRepository {
    private val campaigns = mutableListOf<Campaign>()
    private val mutex = Mutex() // Thread-safe access

    init {
        campaigns.addAll(
            listOf(
                Campaign(CampaignId(UUID.randomUUID()), "Rat Cellar Heroes", "Zeroes to heroes."),
                Campaign(CampaignId(UUID.randomUUID()), "God Killer Chronicles", "Demigods on rampage.")
            )
        )
    }

    override suspend fun save(campaign: Campaign): Campaign = mutex.withLock {
        val newId = campaign.id ?: CampaignId(UUID.randomUUID())
        if (campaigns.any { it.id == newId }) {
            throw IllegalStateException("Cannot duplicate campaign id!")
        }
        val newCampaign = campaign.copy(id = newId)
        campaigns.add(newCampaign)
        newCampaign

    }

    override suspend fun findAll(): List<Campaign> = mutex.withLock {
        campaigns.toList()
    }

    override suspend fun findById(id: CampaignId): Campaign? = mutex.withLock {
        campaigns.find { it.id == id}
    }

    override suspend fun update(campaign: Campaign): Campaign {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: CampaignId): Boolean = mutex.withLock {
        campaigns.removeIf { it.id == id }
    }

}