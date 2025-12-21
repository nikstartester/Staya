package com.xando.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel экрана входа.
 *
 * Отвечает за обработку пользовательского ввода, валидацию данных
 * и управление состоянием [LoginUiState].
 */
internal class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(
        LoginUiState.Idle(
            loginCredentials = LoginCredentials()
        )
    )
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Обновляет email в текущих данных для входа.
     */
    fun onEmailChanged(email: String) {
        updateCredentials { it.copy(email = email) }
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
        if (current !is LoginUiState.Idle) return

        _uiState.value = LoginUiState.Loading(
            loginCredentials = current.loginCredentials
        )

        viewModelScope.launch {
            // TODO: заменить на реальный use case
            delay(1500)

            val success = current.loginCredentials.email.isNotBlank()
                    && current.loginCredentials.password.isNotBlank()

            _uiState.value = if (success) {
                LoginUiState.Success
            } else {
                LoginUiState.Error(
                    loginCredentials = current.loginCredentials,
                    message = "Invalid email or password"
                )
            }
        }
    }

    /**
     * Возвращает экран в состояние ввода данных после отображения ошибки.
     */
    fun onErrorShown() {
        val state = _uiState.value
        if (state is LoginUiState.Error) {
            _uiState.value = LoginUiState.Idle(
                loginCredentials = state.loginCredentials,
                isLoginEnabled = isLoginEnabled(state.loginCredentials)
            )
        }
    }

    /**
     * Обновляет данные для входа и пересчитывает UI-зависимые флаги.
     */
    private fun updateCredentials(
        transform: (LoginCredentials) -> LoginCredentials
    ) {
        _uiState.update { state ->
            when (state) {
                is LoginUiState.Idle -> {
                    val updated = transform(state.loginCredentials)
                    state.copy(
                        loginCredentials = updated,
                        isLoginEnabled = isLoginEnabled(updated)
                    )
                }

                is LoginUiState.Error -> {
                    val updated = transform(state.loginCredentials)
                    LoginUiState.Idle(
                        loginCredentials = updated,
                        isLoginEnabled = isLoginEnabled(updated)
                    )
                }

                else -> state
            }
        }
    }

    /**
     * Проверяет, доступно ли действие входа.
     */
    private fun isLoginEnabled(credentials: LoginCredentials): Boolean {
        return credentials.email.isNotBlank()
                && credentials.password.length >= 6
    }
}