package com.xando.pet_breed_picker.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.xando.core.models.pet.PetBreed
import com.xando.navigation_api.NavigationResultStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel экрана выбора породы питомца.
 *
 * @param requestKey Ключ, под которым вызывающий экран ожидает результат.
 * @param currentBreed Текущая порода для предвыбора.
 * @param navigationResultStore Передача результата вызывающему экрану.
 */
@HiltViewModel(assistedFactory = BreedPickerViewModel.Factory::class)
internal class BreedPickerViewModel @AssistedInject constructor(
    @Assisted private val requestKey: String,
    @Assisted currentBreed: PetBreed?,
    private val navigationResultStore: NavigationResultStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val KEY_STATE = "BreedPickerUiState"
    }

    /**@SelfDocumented*/
    @AssistedFactory
    interface Factory {
        /**@SelfDocumented*/
        fun create(
            requestKey: String,
            currentBreed: PetBreed?,
        ): BreedPickerViewModel
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        BreedPickerUiState(selected = currentBreed)
    )

    /**
     * Состояние экрана выбора породы питомца.
     */
    val uiState: StateFlow<BreedPickerUiState> = _uiState.asStateFlow()

    /**@SelfDocumented*/
    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    /**@SelfDocumented*/
    fun selectBreed(breed: PetBreed) {
        _uiState.update { it.copy(selected = breed) }
    }

    /**
     * Публикует выбранную породу вызывающему экрану.
     */
    fun confirm() {
        _uiState.value.selected?.let { navigationResultStore.setResult(requestKey, it) }
    }
}
