package com.xando.core.database.pet

import androidx.room3.ColumnInfo

/**
 * Профиль питомца — часть [PetEntity] без отметок пользователей.
 *
 * Пишется в `pets` частичным upsert: новая строка получает `null` в полях отметок, а у существующей
 * обновляются только поля профиля.
 *
 * @property id Идентификатор питомца на сервере.
 * @property name Кличка.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property breed Код породы.
 * @property sex Код пола.
 * @property weightGrams Вес в граммах.
 * @property description Описание.
 * @property status Код состояния питомца.
 * @property photoUrl URL фотографии в исходном размере.
 * @property photoThumbnailUrl URL миниатюры фотографии.
 * @property createdAt Момент создания питомца на сервере, epoch millis.
 * @property viewerRole Роль текущего пользователя по отношению к питомцу — одна из [PetViewerRoles].
 */
data class PetProfileEntity(
    val id: String,
    val name: String,
    @ColumnInfo(name = "birth_date") val birthDate: String,
    val breed: String,
    val sex: String,
    @ColumnInfo(name = "weight_grams") val weightGrams: Int,
    val description: String?,
    val status: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String?,
    @ColumnInfo(name = "photo_thumbnail_url") val photoThumbnailUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "viewer_role") val viewerRole: String,
)
