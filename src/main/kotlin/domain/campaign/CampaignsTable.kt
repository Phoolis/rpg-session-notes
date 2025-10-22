package fi.paulcarlson.domain.campaign

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object Campaigns : Table("campaigns") {
    val id = uuid("id").autoGenerate() // Use UUID instead for possible offline mode notes in the future

    val name = varchar("name", 50)
    val description = text("description")

    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val modifiedAt = timestamp("modified_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

fun rowToCampaign(result: ResultRow): Campaign = Campaign(
    id = CampaignId(result[Campaigns.id]),
    name = result[Campaigns.name],
    description = result[Campaigns.description],
    createdAt = result[Campaigns.createdAt],
    modifiedAt = result[Campaigns.modifiedAt]
)