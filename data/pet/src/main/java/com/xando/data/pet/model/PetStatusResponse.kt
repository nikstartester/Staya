package com.xando.data.pet.model

import com.xando.core.models.pet.PetStatus
import kotlinx.serialization.Serializable

/**
 * Сетевое состояние питомца.
 */
@Serializable
internal enum class PetStatusResponse {

    /** Питомец готов к знакомству. */
    READY_TO_SOCIALIZE,

    /** У питомца период течки. */
    IN_HEAT,

    /** К питомцу не следует приближаться. */
    DO_NOT_APPROACH
}

/** Маппинг [PetStatusResponse] в [PetStatus]. */
internal fun PetStatusResponse.mapToDomain() = when (this) {
    PetStatusResponse.READY_TO_SOCIALIZE -> PetStatus.READY_TO_SOCIALIZE
    PetStatusResponse.IN_HEAT -> PetStatus.IN_HEAT
    PetStatusResponse.DO_NOT_APPROACH -> PetStatus.DO_NOT_APPROACH
}
