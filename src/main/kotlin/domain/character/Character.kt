package fi.paulcarlson.domain.character

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.domain.user.UserId
import fi.paulcarlson.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Instant

@JvmInline
@Serializable
value class CharacterId(
    @Serializable(with = UUIDSerializer::class)
    val value: UUID
)

@Serializable
data class Character(
    val id: CharacterId? = null,
    val campaignId: CampaignId,
    val userId: UserId,
    val name: String? = null,
    val role: Role,
    val joinedAt: Instant? = null
)

enum class Role { GM, PLAYER }