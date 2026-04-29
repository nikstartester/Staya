@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet_list.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой ответ со списком питомцев.
 *
 * @property pets Список кратких данных питомцев.
 */
@Serializable
internal data class PetsListResponse(
    val pets: List<PetSummaryResponse>
)
