@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import com.xando.core.models.pet.PetRelation
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевая карточка питомца.
 *
 * @property pet Профиль питомца.
 * @property owners Владельцы: основной первым, затем совладельцы в порядке присоединения, затем — только
 * для основного владельца — приглашённые.
 * @property friendsCount Сколько пользователей отметили, что дружат. Только для хозяев.
 * @property notFriendsCount Сколько пользователей отметили, что не дружат. Только для хозяев.
 * @property friendsPreview До трёх пользователей из тех, кто дружит: сначала с фото, затем отметившие
 * недавно. Только для хозяев.
 * @property notFriendsPreview То же для тех, кто не дружит. Только для хозяев.
 * @property myRelation Код отметки текущего пользователя - имя константы [PetRelation]. Только для
 * чужого питомца; `null`, если отметки нет. Строка, а не enum: новая отметка на сервере не должна
 * ломать разбор карточки в старых версиях приложения.
 */
@Serializable
internal data class PetCardResponse(
    val pet: PetProfileResponse,
    val owners: List<PetOwnerResponse>,
    val friendsCount: Int? = null,
    val notFriendsCount: Int? = null,
    val friendsPreview: List<UserSnippetResponse>? = null,
    val notFriendsPreview: List<UserSnippetResponse>? = null,
    val myRelation: String? = null,
)
