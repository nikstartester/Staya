package com.xando.core.models.pet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Полная информация о питомце: данные для карточки и формы редактирования.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Кличка питомца.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property breed Порода питомца.
 * @property sex Пол питомца.
 * @property weightGrams Вес питомца в граммах.
 * @property interests Список интересов питомца.
 * @property description Описание питомца.
 * @property photoUrl URL фотографии питомца в исходном размере; `null`, если фото нет.
 * @property photoThumbnailUrl URL миниатюры фотографии для списков; `null`, если фото нет.
 * @property status Состояние питомца.
 * @property viewerRole Роль текущего пользователя по отношению к питомцу.
 * @property owners Владельцы.
 */
@Parcelize
data class PetDetail(
    val id: String,
    val name: String,
    val birthDate: String,
    val breed: PetBreed,
    val sex: PetSex,
    val weightGrams: Int,
    val interests: List<PetInterest>,
    val description: String,
    val photoUrl: String?,
    val photoThumbnailUrl: String?,
    val status: PetStatus,
    val viewerRole: PetViewerRole,
    val owners: List<PetOwner>,
) : Parcelable
