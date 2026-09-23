package com.xando.data.pet.model

import kotlinx.serialization.Serializable

/**
 * Сетевая роль текущего пользователя по отношению к питомцу.
 */
@Serializable
internal enum class ViewerRoleResponse {

    /** Основной владелец питомца. */
    OWNER,

    /** Совладелец питомца. */
    CO_OWNER,

    /** Чужой питомец. */
    OTHER
}
