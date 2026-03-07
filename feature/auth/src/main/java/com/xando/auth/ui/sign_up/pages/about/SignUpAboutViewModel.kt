package com.xando.auth.ui.sign_up.pages.about

import androidx.lifecycle.ViewModel
import com.xando.auth.ui.sign_up.SignUpFlowCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**@SelfDocumented*/
internal data class SignUpAboutUiState(
    val description: String = "",
)

/**
 * ViewModel шага "О себе".
 */
@HiltViewModel
internal class SignUpAboutViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
) : ViewModel() {

    /**
     * Одноразовые события шага "О себе".
     */
    sealed interface Event {
        data object NavigateNext : Event
    }

    private val _uiState = MutableStateFlow(SignUpAboutUiState())
    val uiState: StateFlow<SignUpAboutUiState> = _uiState.asStateFlow()
    private val _events = Channel<Event>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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
        _events.trySend(Event.NavigateNext)
    }
}
