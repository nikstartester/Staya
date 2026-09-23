package com.xando.core.models.pet

/**
 * Роль текущего пользователя по отношению к питомцу.
 */
enum class PetViewerRole {

    /** Основной владелец питомца. */
    OWNER,

    /** Совладелец питомца. */
    CO_OWNER,

    /** Чужой питомец. */
    OTHER
}
