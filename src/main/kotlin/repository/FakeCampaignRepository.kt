package fi.paulcarlson.repository

import fi.paulcarlson.model.Campaign

class FakeCampaignRepository: CampaignRepository {
    private val campaigns = mutableListOf(
        Campaign(1, "Rat Cellar Heroes", "Our mighty heroes clear out an infested cellar."),
        Campaign(2, "God Killer Chronicles", "Not even the Almighty is safe.")
    )

    override fun allCampaigns(): List<Campaign> = campaigns

    override fun campaignById(id: Long): Campaign? = campaigns.find {
        it.id == id
    }

    override fun addCampaign(campaign: Campaign) {
        if (campaignById(campaign.id) != null) {
            throw IllegalStateException("Cannot duplicate campaign id!")
        }
        campaigns.add(campaign)
    }

    override fun removeCampaign(id: Long): Boolean {
        return campaigns.removeIf { it.id == id }
    }
}