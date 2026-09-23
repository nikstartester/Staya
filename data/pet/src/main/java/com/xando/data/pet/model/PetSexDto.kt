package com.xando.data.pet.model

import com.xando.core.models.pet.PetSex
import kotlinx.serialization.Serializable

/**
 * Сетевое представление пола питомца: одно и то же в запросах и ответах.
 */
@Serializable
internal enum class PetSexDto {

    /** Кобель. */
    MALE,

    /** Сука. */
    FEMALE
}

/** Маппинг [PetSex] в [PetSexDto]. */
internal fun PetSex.toDto() = when (this) {
    PetSex.MALE -> PetSexDto.MALE
    PetSex.FEMALE -> PetSexDto.FEMALE
}
