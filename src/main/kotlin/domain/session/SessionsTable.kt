package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.campaign.Campaigns
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.*

object Sessions : Table("sessions") {
    val id = uuid("id").autoGenerate()
    val campaignId = reference(
        name = "campaign_id",
        refColumn = Campaigns.id,
        onDelete = ReferenceOption.CASCADE
    )
    val sessionNumber = integer("session_number")
    val sessionDate = date("session_date")

    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val modifiedAt = timestamp("modified_at").nullable()

    override val primaryKey = PrimaryKey(id)

    init {
        index(isUnique = true, campaignId, sessionNumber) // Enforce session number uniqueness per-campaign
    }
}

fun rowToSession(result: ResultRow): Session = Session(
    id = SessionId(result[Sessions.id]),
    campaignId = CampaignId(result[Sessions.campaignId]),
    sessionNumber = result[Sessions.sessionNumber],
    sessionDate = result[Sessions.sessionDate],
    createdAt = result[Sessions.createdAt],
    modifiedAt = result[Sessions.modifiedAt]
)