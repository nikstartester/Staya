@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetDetail
import com.xando.core.models.pet.PetInterest
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой ответ с полной информацией о питомце.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Кличка питомца.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property breed Код породы питомца - имя константы [PetBreed].
 * @property sex Пол питомца.
 * @property weightGrams Вес питомца в граммах.
 * @property interests Коды интересов питомца - имена констант [PetInterest].
 * @property description Описание питомца.
 * @property photoUrl URL фотографии питомца в исходном размере.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 */
@Serializable
internal data class PetCardResponse(
    val id: String,
    val name: String,
    val birthDate: String,
    val breed: String,
    val sex: PetSexDto,
    val weightGrams: Int,
    val interests: List<String>,
    val description: String?,
    val photoUrl: String?,
    val photoThumbnailUrl: String?,
)

/**
 * Маппинг [PetCardResponse] в [PetDetail].
 */
internal fun PetCardResponse.mapToDomain() = PetDetail(
    id = id,
    name = name,
    birthDate = birthDate,
    breed = PetBreed.fromCodeOrNull(breed) ?: PetBreed.MIXED_BREED,
    sex = sex.mapToDomain(),
    weightGrams = weightGrams,
    interests = interests.mapNotNull { PetInterest.fromCodeOrNull(it) },
    description = description ?: "",
    photoUrl = photoUrl ?: "",
    photoThumbnailUrl = photoThumbnailUrl ?: "",
)
