package com.xando.core.database.pet

/**
 * Значения [PetEntity.viewerRole]. Константы, а не enum: по ним фильтруют запросы в [PetDao].
 */
object PetViewerRoles {

    /** Текущий пользователь — основной владелец питомца. */
    const val OWNER = "OWNER"

    /** Текущий пользователь — совладелец питомца. */
    const val CO_OWNER = "CO_OWNER"

    /** Чужой питомец. */
    const val OTHER = "OTHER"
}
