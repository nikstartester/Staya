package com.xando.core.models.pet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Краткая информация о питомце.
 *
 * @property id Уникальный идентификатор питомца.
 * @property name Имя питомца.
 * @property breed Порода питомца.
 * @property photoUrl URL фотографии питомца.
 * @property status Состояние питомца.
 * @property isOwner Признак того, что текущий пользователь является владельцем питомца.
 */
@Parcelize
data class PetSummary(
    val id: String,
    val name: String,
    val breed: String,
    val photoUrl: String,
    val status: PetStatus,
    val isOwner: Boolean
) : Parcelable
