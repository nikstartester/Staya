@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой ответ со списком питомцев текущего пользователя.
 *
 * @property pets Данные питомцев.
 */
@Serializable
internal data class PetsListResponse(
    val pets: List<PetProfileResponse>
)
