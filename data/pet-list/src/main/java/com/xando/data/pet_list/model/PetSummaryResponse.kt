@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet_list.model

import com.xando.core.models.pet.PetStatus
import com.xando.core.models.pet.PetSummary
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой ответ с краткой информацией о питомце.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Имя питомца.
 * @property breed Порода питомца.
 * @property photoUrl URL фотографии питомца.
 * @property status Сетевое состояние питомца.
 * @property isOwner Признак того, что текущий пользователь является владельцем питомца.
 */
@Serializable
internal data class PetSummaryResponse(
    val id: String,
    val name: String,
    val breed: String?,
    val photoUrl: String?,
    val status: PetStatusResponse,
    val isOwner: Boolean
)

/**
 * Сетевое состояние питомца.
 */
internal enum class PetStatusResponse {
    /**
     * Питомец готов к знакомству.
     */
    READY_TO_SOCIALIZE,

    /**
     * У питомца период течки.
     */
    IN_HEAT,

    /**
     * К питомцу не следует приближаться.
     */
    DO_NOT_APPROACH
}

/** Маппинг [PetSummaryResponse] в [PetSummary]. */
internal fun PetSummaryResponse.mapToDomain() = PetSummary(
    id = id,
    name = name,
    breed = breed ?: "",
    photoUrl = photoUrl ?: "",
    status = status.mapToDomain(),
    isOwner = isOwner
)

private fun PetStatusResponse.mapToDomain() =
    when (this) {
        PetStatusResponse.READY_TO_SOCIALIZE -> PetStatus.READY_TO_SOCIALIZE
        PetStatusResponse.IN_HEAT -> PetStatus.IN_HEAT
        PetStatusResponse.DO_NOT_APPROACH -> PetStatus.DO_NOT_APPROACH
    }
