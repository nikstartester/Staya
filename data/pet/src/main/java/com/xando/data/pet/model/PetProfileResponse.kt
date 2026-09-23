@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.database.pet.PetViewerRoles
import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetStatus
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевая модель питомца.
 *
 * Коды, которые сервер может расширить, приходят строками, а не enum: новый код не должен ломать разбор
 * всего ответа в старых версиях приложения.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Кличка питомца.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property breed Код породы питомца - имя константы [PetBreed].
 * @property sex Пол питомца.
 * @property weightGrams Вес питомца в граммах.
 * @property interests Коды интересов питомца - имена констант [PetInterest].
 * @property description Описание питомца.
 * @property status Код состояния питомца - имя константы [PetStatus].
 * @property photoUrl URL фотографии питомца в исходном размере.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 * @property createdAt Момент создания питомца на сервере в формате ISO-8601 со смещением.
 * @property viewerRole Код роли текущего пользователя по отношению к питомцу - одна из [PetViewerRoles].
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
    val status: String,
    val photoUrl: String?,
    val photoThumbnailUrl: String?,
    val createdAt: String,
    val viewerRole: String,
)
