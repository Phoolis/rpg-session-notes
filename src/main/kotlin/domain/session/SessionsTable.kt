package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.campaign.Campaigns
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.*

object Sessions : Table() {
    val id = uuid("id").autoGenerate()
    val campaignId = reference(
        name = "campaign_id",
        refColumn = Campaigns.id,
        onDelete = ReferenceOption.CASCADE
    )
    val sessionNumber = integer("session_number")
    val sessionDate = date("session_date")

    override val primaryKey = PrimaryKey(id)

    init {
        index(isUnique = true, campaignId, sessionNumber) // Enforce per-campaign uniqueness
    }
}

fun rowToSession(result: ResultRow): Session = Session(
    id = SessionId(result[Sessions.id]),
    campaignId = CampaignId(result[Sessions.campaignId]),
    sessionNumber = result[Sessions.sessionNumber],
    sessionDate = result[Sessions.sessionDate]
)