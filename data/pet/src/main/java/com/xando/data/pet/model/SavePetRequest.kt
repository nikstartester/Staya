@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetDraft
import com.xando.core.models.pet.PetDraftPhoto
import com.xando.core.models.pet.PetInterest
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой запрос на создание или обновление питомца: тело у них одно и то же, различается только
 * адрес.
 *
 * @property name Кличка питомца.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property weightGrams Вес питомца в граммах.
 * @property breed Код породы питомца - имя константы [PetBreed].
 * @property sex Пол питомца.
 * @property interests Коды интересов питомца - имена констант [PetInterest].
 * @property description Описание питомца.
 * @property photoFileId Идентификатор загруженного файла с новым фото питомца.
 * @property removePhoto Признак удаления текущего фото питомца; вместе с [photoFileId] не передаётся.
 */
@Serializable
internal data class SavePetRequest(
    val name: String,
    val birthDate: String,
    val weightGrams: Int,
    val breed: String,
    val sex: PetSexDto,
    val interests: List<String> = emptyList(),
    val description: String? = null,
    val photoFileId: String? = null,
    val removePhoto: Boolean = false,
)

/**
 * Маппинг [PetDraft] в [SavePetRequest].
 *
 * @param photoFileId Идентификатор уже загруженного файла с новым фото; `null`, если фото не
 * менялось или удаляется.
 */
internal fun PetDraft.toRequest(photoFileId: String?) = SavePetRequest(
    name = name,
    birthDate = birthDate,
    weightGrams = weightGrams,
    breed = breed.name,
    sex = sex.toDto(),
    interests = interests.map(PetInterest::name),
    description = description,
    photoFileId = photoFileId,
    removePhoto = photo is PetDraftPhoto.Remove,
)
