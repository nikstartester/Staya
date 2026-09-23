package com.xando.pets_list.presentation

import com.xando.core.models.pet.PetSummary
import com.xando.design.ui.snackbar.StayaSnackbarData

/**
 * Состояние экрана списка питомцев.
 *
 * @property pets Список питомцев.
 * @property isLoading Признак обновления списка питомцев.
 * @property isEmptyStubVisible Признак показа заглушки пустого списка.
 */
data class PetListUiState(
    val pets: List<PetSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isEmptyStubVisible: Boolean = false,
)

/**
 * Одноразовые события экрана списка питомцев.
 */
sealed interface PetListEvent {
    /**
     * Событие показа snackbar.
     *
     * @property snackbarData Данные snackbar.
     */
    data class ShowSnackbar(val snackbarData: StayaSnackbarData) : PetListEvent
}
