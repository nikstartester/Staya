package com.xando.core.database.user

import androidx.room3.ColumnInfo

/**
 * Часть [UserEntity], которая приходит с сервера вместе с другими сущностями, например в списке
 * владельцев питомца.
 *
 * Пишется в `users` частичным upsert: новая строка получает `null` в полях полного профиля, а у
 * существующей обновляются только эти поля, не затирая остальные.
 *
 * @property id Идентификатор пользователя на сервере.
 * @property firstName Имя.
 * @property lastName Фамилия.
 * @property login Логин; `null`, если пользователь его не задал.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 */
data class UserSnippetEntity(
    val id: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    val login: String?,
    @ColumnInfo(name = "photo_thumbnail_url") val photoThumbnailUrl: String?,
)
