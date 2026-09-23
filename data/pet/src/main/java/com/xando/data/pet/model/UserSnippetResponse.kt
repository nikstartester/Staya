@file:OptIn(InternalSerializationApi::class)

package com.xando.data.pet.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Сетевые краткие данные пользователя.
 *
 * @property id Уникальный идентификатор пользователя.
 * @property firstName Имя.
 * @property lastName Фамилия.
 * @property login Логин; `null`, если пользователь его не задал.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 */
@Serializable
internal data class UserSnippetResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val login: String?,
    val photoThumbnailUrl: String?,
)
