package com.xando.core.models.pet

/**
 * Состояние питомца.
 */
enum class PetStatus {
    /**
     * Питомец готов к знакомству.
     */
    READY_TO_SOCIALIZE,

    /**
     * У питомца период течки.
     */
    IN_HEAT,

    /**
     * К питомцу не следует приближаться.
     */
    DO_NOT_APPROACH
}
