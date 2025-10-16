package fi.paulcarlson.repository

import fi.paulcarlson.model.Campaign

interface CampaignRepository {
    fun allCampaigns(): List<Campaign>
    fun campaignById(id: Long): Campaign?
    fun addCampaign(campaign: Campaign)
    fun removeCampaign(id: Long): Boolean
}