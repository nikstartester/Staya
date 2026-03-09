package com.xando.auth.ui.sign_up.pages.about

import androidx.lifecycle.ViewModel
import com.xando.auth.ui.sign_up.SignUpFlowCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel шага "О себе".
 */
@HiltViewModel
internal class SignUpAboutViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpAboutUiState())

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
        coordinator.updateAbout(_uiState.value.description)
        _events.trySend(SignUpAboutEvent.NavigateNext)
    }
}

/**@SelfDocumented*/
internal data class SignUpAboutUiState(
    val description: String = "",
)

/**
 * Одноразовые события шага "О себе".
 */
sealed interface SignUpAboutEvent {
    data object NavigateNext : SignUpAboutEvent
}