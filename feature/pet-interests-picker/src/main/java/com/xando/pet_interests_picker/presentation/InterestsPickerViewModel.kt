package com.xando.pet_interests_picker.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.xando.core.models.pet.PetInterest
import com.xando.navigation_api.NavigationResultStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel экрана выбора интересов питомца.
 *
 * @param requestKey Ключ, под которым вызывающий экран ожидает результат.
 * @param currentInterests Текущий выбор интересов для предвыбора.
 * @param maxSelection Максимальное количество интересов, которое можно выбрать одновременно.
 * @param navigationResultStore Передача результата вызывающему экрану.
 */
@HiltViewModel(assistedFactory = InterestsPickerViewModel.Factory::class)
internal class InterestsPickerViewModel @AssistedInject constructor(
    @Assisted private val requestKey: String,
    @Assisted currentInterests: List<PetInterest>,
    @Assisted maxSelection: Int,
    private val navigationResultStore: NavigationResultStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val KEY_STATE = "InterestsPickerUiState"
    }

    /**@SelfDocumented*/
    @AssistedFactory
    interface Factory {
        /**@SelfDocumented*/
        fun create(
            requestKey: String,
            currentInterests: List<PetInterest>,
            maxSelection: Int,
        ): InterestsPickerViewModel
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        InterestsPickerUiState(selected = currentInterests, maxSelection = maxSelection)
    )

    /**
     * Состояние экрана выбора интересов питомца.
     */
    val uiState: StateFlow<InterestsPickerUiState> = _uiState.asStateFlow()

    private val _events = Channel<InterestsPickerEvent>(capacity = Channel.UNLIMITED)

    /**
     * Одноразовые события экрана выбора интересов питомца.
     */
    val events: Flow<InterestsPickerEvent> = _events.receiveAsFlow()

    /**@SelfDocumented*/
    fun setQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    /**
     * Переключает выбор интереса [interest]. Если интерес ещё не выбран и лимит
     * [InterestsPickerUiState.maxSelection] уже достигнут, выбор не меняется, а экрану уходит
     * [InterestsPickerEvent.LimitReached].
     */
    fun toggleInterest(interest: PetInterest) {
        val state = _uiState.value
        when {
            interest in state.selected -> _uiState.update { it.copy(selected = it.selected - interest) }
            state.isLimitReached -> _events.trySend(InterestsPickerEvent.LimitReached)
            else -> _uiState.update { it.copy(selected = it.selected + interest) }
        }
    }

    /**
     * Публикует выбранные интересы вызывающему экрану.
     */
    fun confirm() {
        navigationResultStore.setResult(requestKey, _uiState.value.selected)
    }
}
