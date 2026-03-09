package com.xando.auth.ui.sign_up.pages.about

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.xando.auth.ui.sign_up.SignUpFlowCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * ViewModel шага "О себе".
 */
@HiltViewModel
internal class SignUpAboutViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "SignUpAboutUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(KEY_STATE, SignUpAboutUiState())

    /**@SelfDocumented*/
    val uiState: StateFlow<SignUpAboutUiState> = _uiState.asStateFlow()

    private val _events = Channel<SignUpAboutEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpAboutEvent> = _events.receiveAsFlow()

    /**
     * Обновляет описание пользователя.
     */
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        updateCoordinator()
        _events.trySend(SignUpAboutEvent.NavigateNext)
    }

    private fun updateCoordinator() {
        val state = _uiState.value
        coordinator.updateAbout(state.description)
    }
}

/**@SelfDocumented*/
@Parcelize
internal data class SignUpAboutUiState(
    val description: String = "",
) : Parcelable

/**
 * Одноразовые события шага "О себе".
 */
sealed interface SignUpAboutEvent {
    data object NavigateNext : SignUpAboutEvent
}