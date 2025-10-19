package fi.paulcarlson.domain.campaign

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

object Campaigns : Table() {
    val id = uuid("id").autoGenerate() // Use UUID instead for possible offline mode notes in the future

    val name = varchar("name", 50)
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}

fun rowToCampaign(result: ResultRow): Campaign = Campaign(
    id = CampaignId(result[Campaigns.id]),
    name = result[Campaigns.name],
    description = result[Campaigns.description]
)