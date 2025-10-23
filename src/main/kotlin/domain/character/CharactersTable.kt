package fi.paulcarlson.domain.character

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.campaign.Campaigns
import fi.paulcarlson.domain.user.UserId
import fi.paulcarlson.domain.user.Users
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object Characters : Table("characters") {
    val id = uuid("id").autoGenerate()

    val campaignId = reference(
        name = "campaign_id",
        refColumn = Campaigns.id,
        onDelete = ReferenceOption.CASCADE)
    val userId = reference(
        name = "user_id",
        refColumn = Users.id,
        onDelete = ReferenceOption.CASCADE
    )
    val name = varchar("name", 50).nullable()
    val role = enumerationByName<Role>("role", 16).default(Role.PLAYER)
    val joinedAt = timestamp("joined_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(campaignId, userId) // Enforce that one user only has one reference to the campaign
    }
}

fun rowToCharacter(result: ResultRow): Character = Character(
    id = CharacterId(result[Characters.id]),
    campaignId = CampaignId(result[Characters.campaignId]),
    userId = UserId(result[Characters.userId]),
    name = result[Characters.name],
    role = result[Characters.role],
    joinedAt = result[Characters.joinedAt]
)