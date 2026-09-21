package com.xando.core.models.pet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Краткая информация о питомце.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Имя питомца.
 * @property breed Порода питомца.
 * @property photoUrl URL фотографии питомца в исходном размере.
 * @property photoThumbnailUrl URL миниатюры фотографии для списков.
 * @property status Состояние питомца.
 * @property isOwner Признак того, что текущий пользователь является владельцем питомца.
 */
@Parcelize
data class PetSummary(
    val id: String,
    val name: String,
    val breed: PetBreed,
    val photoUrl: String,
    val photoThumbnailUrl: String,
    val status: PetStatus,
    val isOwner: Boolean
) : Parcelable
