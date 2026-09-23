package com.xando.core.models.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Краткая информация о пользователе: для списков и упоминаний.
 *
 * @property id Уникальный идентификатор пользователя.
 * @property firstName Имя.
 * @property lastName Фамилия.
 * @property login Логин; `null`, если пользователь его не задал.
 * @property photoThumbnailUrl URL миниатюры фотографии; пустая строка, если фото нет.
 */
@Parcelize
data class UserSummary(
    val id: String,
    val firstName: String,
    val lastName: String,
    val login: String,
    val photoThumbnailUrl: String,
) : Parcelable
