package com.xando.auth.ui.sign_up.pages.login

import androidx.lifecycle.ViewModel
import com.xando.auth.ui.sign_up.SignUpFlowCoordinator
import com.xando.auth.ui.sign_up.components.BottomSectionAction
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
 * ViewModel шага выбора логина.
 */
@HiltViewModel
internal class SignUpLoginViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
) : ViewModel() {

    companion object {
        private val LOGIN_REGEX = Regex("^[A-Za-z0-9_]{$MIN_LOGIN_LENGTH,}$")
        private const val MIN_LOGIN_LENGTH = 4
    }

    private val _uiState = MutableStateFlow(
        SignUpLoginUiState(minLoginLength = MIN_LOGIN_LENGTH)
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<SignUpLoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<SignUpLoginEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpLoginEvent> = _events.receiveAsFlow()

    /**
     * Обновляет логин и очищает ошибку поля.
     */
    fun updateLogin(login: String) {
        val action = if (login.isBlank()) BottomSectionAction.SKIP else BottomSectionAction.CONTINUE
        _uiState.update {
            it.copy(
                login = login,
                loginError = null,
                action = action
            )
        }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        if (!validate()) return

        coordinator.updateLogin(_uiState.value.login)
        _events.trySend(SignUpLoginEvent.NavigateNext)
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

/**@SelfDocumented*/
internal data class SignUpLoginUiState(
    val login: String = "",
    val loginError: String? = null,
    val action: BottomSectionAction = BottomSectionAction.SKIP,
    val minLoginLength: Int = 4
)

/**
 * Одноразовые события шага ввода логина.
 */
sealed interface SignUpLoginEvent {
    data object NavigateNext : SignUpLoginEvent
}