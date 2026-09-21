package com.xando.pet_breed_picker.presentation

import android.os.Parcelable
import com.xando.core.models.pet.PetBreed
import kotlinx.parcelize.Parcelize

/**
 * Состояние экрана выбора породы питомца.
 *
 * @property query Строка поиска.
 * @property selected Выбранная порода.
 */
@Parcelize
internal data class BreedPickerUiState(
    val query: String = "",
    val selected: PetBreed? = null,
) : Parcelable
