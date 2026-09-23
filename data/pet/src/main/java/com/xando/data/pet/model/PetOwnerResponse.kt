@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевое место пользователя в списке владельцев питомца.
 *
 * @property user Пользователь.
 * @property role Роль пользователя в списке владельцев.
 */
@Serializable
internal data class PetOwnerResponse(
    val user: UserSnippetResponse,
    val role: PetOwnerRoleResponse,
)

/**
 * Сетевая роль пользователя в списке владельцев питомца.
 */
@Serializable
internal enum class PetOwnerRoleResponse {

    /** Основной владелец питомца. */
    OWNER,

    /** Совладелец питомца. */
    CO_OWNER,

    /** Приглашён в совладельцы, но ещё не принял приглашение. Виден только основному владельцу. */
    INVITED
}
