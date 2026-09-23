@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetDetail
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetSummary
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевая модель питомца.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Кличка питомца.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property breed Код породы питомца - имя константы [PetBreed].
 * @property sex Пол питомца.
 * @property weightGrams Вес питомца в граммах.
 * @property interests Коды интересов питомца - имена констант [PetInterest].
 * @property description Описание питомца.
 * @property status Состояние питомца.
 * @property photoUrl URL фотографии питомца в исходном размере.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 * @property createdAt Момент создания питомца на сервере в формате ISO-8601 со смещением.
 * @property viewerRole Роль текущего пользователя по отношению к питомцу.
 */
@Serializable
internal data class PetProfileResponse(
    val id: String,
    val name: String,
    val birthDate: String,
    val breed: String,
    val sex: PetSexDto,
    val weightGrams: Int,
    val interests: List<String>,
    val description: String?,
    val status: PetStatusResponse,
    val photoUrl: String?,
    val photoThumbnailUrl: String?,
    val createdAt: String,
    val viewerRole: ViewerRoleResponse,
)

/**
 * Маппинг [PetProfileResponse] в [PetSummary].
 */
internal fun PetProfileResponse.mapToSummary() = PetSummary(
    id = id,
    name = name,
    breed = PetBreed.fromCodeOrNull(breed) ?: PetBreed.MIXED_BREED,
    photoUrl = photoUrl ?: "",
    photoThumbnailUrl = photoThumbnailUrl ?: "",
    status = status.mapToDomain(),
    isOwner = viewerRole == ViewerRoleResponse.OWNER,
)

/**
 * Маппинг [PetProfileResponse] в [PetDetail].
 */
internal fun PetProfileResponse.mapToDetail() = PetDetail(
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
