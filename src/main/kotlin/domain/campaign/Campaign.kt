package fi.paulcarlson.domain.campaign

import fi.paulcarlson.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@JvmInline
@Serializable
value class CampaignId(
    @Serializable(with = UUIDSerializer::class)
    val value: UUID
)

@Serializable
data class Campaign(
    val id: CampaignId? = null,
    val name: String,
    val description: String,
)