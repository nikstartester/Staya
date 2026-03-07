package com.xando.auth.ui.sign_up.pages.email_password

import android.util.Patterns
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
internal data class SignUpEmailPasswordUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
) {
    val showPasswordButtonVisible: Boolean
        get() = password.isNotEmpty() || repeatPassword.isNotEmpty()
}

/**
 * ViewModel шага ввода e-mail и пароля.
 */
@HiltViewModel
internal class SignUpEmailPasswordViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
) : ViewModel() {

    /**
     * Одноразовые события шага ввода e-mail и пароля.
     */
    sealed interface Event {
        data object NavigateNext : Event
    }

    private val _uiState = MutableStateFlow(SignUpEmailPasswordUiState())
    val uiState: StateFlow<SignUpEmailPasswordUiState> = _uiState.asStateFlow()
    private val _events = Channel<Event>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Обновляет e-mail и очищает ошибку поля.
     */
    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
            )
        }
    }

    /**
     * Обновляет пароль и очищает связанные ошибки.
     */
    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                repeatPasswordError = if (it.repeatPassword.isEmpty()) null else it.repeatPasswordError,
            )
        }
    }

    /**
     * Обновляет повтор пароля и очищает ошибку поля.
     */
    fun updateRepeatPassword(repeatPassword: String) {
        _uiState.update {
            it.copy(
                repeatPassword = repeatPassword,
                repeatPasswordError = null,
            )
        }
    }

    /**
     * Переключает видимость пароля.
     */
    fun updatePasswordVisibility(isPasswordVisible: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = isPasswordVisible) }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        if (!validate()) return

        val state = _uiState.value
        coordinator.updateEmailPassword(
            email = state.email,
            password = state.password,
            repeatPassword = state.repeatPassword,
        )
        _events.trySend(Event.NavigateNext)
    }

    /**
     * Проверяет корректность e-mail и паролей перед переходом дальше.
     */
    private fun validate(): Boolean {
        val state = _uiState.value

        val emailError = when {
            state.email.isBlank() -> ""
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> ""
            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> ""
            state.password.length < 6 -> ""
            else -> null
        }

        val repeatPasswordError = when {
            state.repeatPassword.isBlank() -> ""
            state.password != state.repeatPassword -> ""
            else -> null
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                repeatPasswordError = repeatPasswordError,
            )
        }

        return emailError == null && passwordError == null && repeatPasswordError == null
    }
}
