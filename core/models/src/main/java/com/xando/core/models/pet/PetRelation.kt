package com.xando.core.models.pet

import android.os.Parcelable
import com.xando.core.models.user.UserSummary
import kotlinx.parcelize.Parcelize

/**
 * Отметка пользователя о чужом питомце: дружат ли с ним его собаки.
 */
enum class PetRelation {

    /** Дружат. */
    FRIENDS,

    /** Не дружат. */
    NOT_FRIENDS
}

/**
 * Отметки пользователей о питомце.
 */
sealed interface PetRelations : Parcelable {

    /**
     * Сводка отметок для хозяев питомца.
     *
     * @property friendsCount Сколько пользователей отметили, что дружат.
     * @property notFriendsCount Сколько пользователей отметили, что не дружат.
     * @property friendsPreview Несколько пользователей из тех, кто дружит.
     * @property notFriendsPreview Несколько пользователей из тех, кто не дружит.
     */
    @Parcelize
    data class OwnerView(
        val friendsCount: Int,
        val notFriendsCount: Int,
        val friendsPreview: List<UserSummary>,
        val notFriendsPreview: List<UserSummary>,
    ) : PetRelations

    /**
     * Отметка текущего пользователя о чужом питомце.
     *
     * @property myRelation Отметка; `null`, если её нет.
     */
    @Parcelize
    data class OtherView(
        val myRelation: PetRelation?,
    ) : PetRelations
}
