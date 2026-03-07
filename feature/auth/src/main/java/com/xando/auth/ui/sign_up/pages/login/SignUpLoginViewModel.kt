package com.xando.auth.ui.sign_up.pages.login

import androidx.lifecycle.ViewModel
import com.xando.auth.ui.sign_up.SignUpFlowCoordinator
import com.xando.auth.ui.sign_up.components.BottomSectionAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private val LOGIN_REGEX = Regex("^[A-Za-z0-9_]{4,}$")

/**@SelfDocumented*/
internal data class SignUpLoginUiState(
    val login: String = "",
    val loginError: String? = null,
) {
    val action = if (login.isBlank()) BottomSectionAction.SKIP
    else BottomSectionAction.CONTINUE
}

/**
 * ViewModel шага выбора логина.
 */
@HiltViewModel
internal class SignUpLoginViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
) : ViewModel() {

    /**
     * Одноразовые события шага выбора логина.
     */
    sealed interface Event {
        data object NavigateNext : Event
    }

    private val _uiState = MutableStateFlow(SignUpLoginUiState())
    val uiState: StateFlow<SignUpLoginUiState> = _uiState.asStateFlow()
    private val _events = Channel<Event>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Обновляет логин и очищает ошибку поля.
     */
    fun updateLogin(login: String) {
        _uiState.update {
            it.copy(
                login = login,
                loginError = null,
            )
        }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        if (!validate()) return

        coordinator.updateLogin(_uiState.value.login)
        _events.trySend(Event.NavigateNext)
    }

    /**
     * Проверяет логин по правилам экрана перед переходом дальше.
     */
    private fun validate(): Boolean {
        val login = _uiState.value.login

        if (login.isBlank()) return true

        val loginError = if (LOGIN_REGEX.matches(login)) null
        else ""

        _uiState.update { it.copy(loginError = loginError) }

        return loginError == null
    }
}
