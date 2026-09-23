@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.models.pet.PetOwnerRole
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевое место пользователя в списке владельцев питомца.
 *
 * @property user Пользователь.
 * @property role Код роли пользователя в списке владельцев - имя константы [PetOwnerRole]. Строка, а не
 * enum: новая роль на сервере не должна ломать разбор карточки в старых версиях приложения.
 */
@Serializable
internal data class PetOwnerResponse(
    val user: UserSnippetResponse,
    val role: String,
)
