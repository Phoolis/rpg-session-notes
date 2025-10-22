package fi.paulcarlson.domain.session

import fi.paulcarlson.domain.campaign.CampaignId
import fi.paulcarlson.util.UUIDSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Instant

@JvmInline
@Serializable
value class SessionId(
    @Serializable(with = UUIDSerializer::class)
    val value: UUID
)

@Serializable
data class Session(
    val id: SessionId? = null,
    val campaignId: CampaignId,
    val sessionNumber: Int? = null,
    val sessionDate: LocalDate,

    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null
)