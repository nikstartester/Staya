package com.xando.pets_list.presentation

import android.os.Parcelable
import com.xando.core.models.pet.PetSummary
import com.xando.design.ui.snackbar.StayaSnackbarData
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Состояние экрана списка питомцев.
 *
 * @property pets Список питомцев.
 * @property isLoading Признак загрузки списка питомцев. Не сохраняется при смерти процесса — запрос её не переживает.
 */
@Parcelize
data class PetListUiState(
    val pets: List<PetSummary> = emptyList(),
    @IgnoredOnParcel
    val isLoading: Boolean = false
) : Parcelable

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
