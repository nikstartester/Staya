@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевая карточка питомца.
 *
 * Сервер отдаёт в карточке ещё и отношения пользователей к питомцу.
 *
 * @property pet Профиль питомца.
 * @property owners Владельцы: основной первым, затем совладельцы в порядке присоединения, затем — только
 * для основного владельца — приглашённые.
 */
@Serializable
internal data class PetCardResponse(
    val pet: PetProfileResponse,
    val owners: List<PetOwnerResponse>,
)
