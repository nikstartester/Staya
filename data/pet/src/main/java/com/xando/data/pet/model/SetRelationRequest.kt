@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.models.pet.PetRelation
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевой запрос на отметку о чужом питомце.
 *
 * @property relation Код отметки - имя константы [PetRelation].
 */
@Serializable
internal data class SetRelationRequest(
    val relation: String,
)
