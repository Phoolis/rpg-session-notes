package fi.paulcarlson.domain.campaign

import io.ktor.server.plugins.NotFoundException
import java.util.UUID

class CampaignService(
    private val campaignRepository: CampaignRepository
) {
    suspend fun createCampaign(campaign: Campaign): Campaign {
        return campaignRepository.save(campaign)
    }

    suspend fun getCampaigns(): List<Campaign> {
        return campaignRepository.findAll()
    }

    suspend fun getCampaign(id: UUID): Campaign? {
        return campaignRepository.findById(CampaignId(id))
    }

    suspend fun editCampaign(campaign: Campaign): Campaign {
        val existing = campaignRepository.findById(campaign.id!!)
            ?: throw NotFoundException("Campaign not found")

        // Only allow changing campaign name and description
        val updated = existing.copy(
            name = campaign.name,
            description = campaign.description
        )
        return campaignRepository.update(updated)
    }

    suspend fun removeCampaign(id: UUID): Boolean {
        return campaignRepository.delete(CampaignId(id))
    }
}