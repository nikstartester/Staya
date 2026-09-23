package com.xando.core.database.user

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Публичные данные пользователя: одна строка на пользователя, откуда бы он ни пришёл — владелец
 * питомца, участник чата и т.п.
 *
 * Поля полного профиля заполняются только из профиля пользователя, поэтому остальные источники
 * пишут в таблицу через [UserSnippetEntity].
 *
 * @property id Идентификатор пользователя на сервере.
 * @property firstName Имя.
 * @property lastName Фамилия.
 * @property login Логин; `null`, если пользователь его не задал.
 * @property photoUrl URL фотографии в исходном размере. Поле полного профиля.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 * @property description Описание. Поле полного профиля.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    val login: String?,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "photo_thumbnail_url") val photoThumbnailUrl: String?,
    val description: String?,
)
