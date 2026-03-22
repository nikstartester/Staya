package com.xando.auth.ui.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.auth.domain.validation.AuthValidationRules
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel экрана входа.
 *
 * Отвечает за обработку пользовательского ввода, валидацию данных
 * и управление состоянием [LoginUiState].
 */
@HiltViewModel
internal class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "LoginUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        LoginUiState()
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _events = Channel<LoginEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    /**
     * Обновляет email в текущих данных для входа.
     */
    fun onEmailChanged(email: String) {
        updateCredentials { it.copy(login = email) }
    }

    /**
     * Обновляет пароль в текущих данных для входа.
     */
    fun onPasswordChanged(password: String) {
        updateCredentials { it.copy(password = password) }
    }

    /**
     * Запускает процесс авторизации с текущими данными.
     */
    fun onLoginClick() {
        val current = _uiState.value
        if (current.isLoading) return
        _uiState.value = current.copy(isLoading = true)

        viewModelScope.launch {
            // TODO: заменить на реальный use case
            delay(1500)

            val success = current.loginCredentials.login.isNotBlank()
                && current.loginCredentials.password.isNotBlank()

            _uiState.value = if (success) {
                _events.trySend(LoginEvent.LoginSuccess)
                current.copy(
                    isLoading = false,
                    isLoginEnabled = isLoginEnabled(current.loginCredentials)
                )
            } else {
                _events.trySend(LoginEvent.ShowError("Invalid email or password"))
                current.copy(
                    isLoading = false,
                    isLoginEnabled = isLoginEnabled(current.loginCredentials)
                )
            }
        }
    }

    /**
     * Обновляет данные для входа и пересчитывает UI-зависимые флаги.
     */
    private fun updateCredentials(
        transform: (LoginCredentials) -> LoginCredentials
    ) {
        _uiState.update { state ->
            if (state.isLoading) {
                state
            } else {
                val updated = transform(state.loginCredentials)
                state.copy(
                    loginCredentials = updated,
                    isLoginEnabled = isLoginEnabled(updated)
                )
            }
        }
    }

    /**
     * Проверяет, доступно ли действие входа.
     */
    private fun isLoginEnabled(credentials: LoginCredentials): Boolean {
        return credentials.login.isNotBlank()
            && credentials.password.length >= AuthValidationRules.MIN_PASSWORD_LENGTH
    }
}
