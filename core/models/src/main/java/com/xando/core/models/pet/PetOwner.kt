package com.xando.core.models.pet

import android.os.Parcelable
import com.xando.core.models.user.UserSummary
import kotlinx.parcelize.Parcelize

/**
 * Пользователь в списке владельцев питомца.
 *
 * @property user Пользователь.
 * @property role Роль пользователя в списке владельцев.
 */
@Parcelize
data class PetOwner(
    val user: UserSummary,
    val role: PetOwnerRole,
) : Parcelable

/**
 * Роль пользователя в списке владельцев питомца.
 */
enum class PetOwnerRole {

    /** Основной владелец питомца. */
    OWNER,

    /** Совладелец питомца. */
    CO_OWNER,

    /** Приглашён в совладельцы, но ещё не принял приглашение. Виден только основному владельцу. */
    INVITED
}
