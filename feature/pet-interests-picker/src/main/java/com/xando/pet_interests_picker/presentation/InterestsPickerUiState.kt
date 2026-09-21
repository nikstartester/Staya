package com.xando.pet_interests_picker.presentation

import android.os.Parcelable
import com.xando.core.models.pet.PetInterest
import kotlinx.parcelize.Parcelize

/**
 * Состояние экрана выбора интересов питомца.
 *
 * @property query Строка поиска.
 * @property selected Выбранные интересы.
 * @property maxSelection Максимальное количество интересов, которое можно выбрать одновременно.
 */
@Parcelize
internal data class InterestsPickerUiState(
    val query: String = "",
    val selected: List<PetInterest> = emptyList(),
    val maxSelection: Int = Int.MAX_VALUE,
) : Parcelable {

    /** Признак того, что выбрано максимально допустимое количество интересов. */
    val isLimitReached: Boolean get() = selected.size >= maxSelection
}

/**
 * Одноразовые события экрана выбора интересов питомца.
 */
internal sealed interface InterestsPickerEvent {

    /**
     * Попытка выбрать интерес сверх лимита [InterestsPickerUiState.maxSelection].
     */
    data object LimitReached : InterestsPickerEvent
}
