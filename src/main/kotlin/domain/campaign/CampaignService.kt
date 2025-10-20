package fi.paulcarlson.domain.campaign

import java.util.UUID

class CampaignService(
    private val campaignRepository: CampaignRepository
) {
    suspend fun createCampaign(campaign: Campaign): Campaign {
        val id = campaignRepository.save(campaign)
        return campaign.copy(id = id)
    }

    suspend fun getCampaigns(): List<Campaign> {
        return campaignRepository.findAll()
    }

    suspend fun getCampaign(id: UUID): Campaign? {
        return campaignRepository.findById(CampaignId(id))
    }

    suspend fun editCampaign(campaign: Campaign): Campaign {
        // TODO: Add Error handling with custom exceptions. For now, we just propagate errors up to the routes layer.
        return campaignRepository.update(campaign)
    }

    suspend fun removeCampaign(id: UUID): Boolean {
        return campaignRepository.delete(CampaignId(id))
    }
}